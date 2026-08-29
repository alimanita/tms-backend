package com.transport.tms.service.fleet;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.opencv.opencv_core.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * Pipeline complet de traitement d'un justificatif image avant stockage :
 *  1. Decode l'image source
 *  2. Detection du contour du ticket (Canny + fermeture morphologique)
 *  3. Approximation polygonale (4 coins reels)
 *  4. Transformation de perspective (warpPerspective)
 *  5. Recadrage fin
 *  6. Amelioration type scan (CLAHE + nettete)
 *  7. Compression WebP qualite 75
 */
@Slf4j
public class ImageCompressionUtil {

    private ImageCompressionUtil() {}

    // =========================================================================
    // POINT D'ENTREE PRINCIPAL
    // =========================================================================

    public static void compressToWebP(InputStream inputStream, Path target, float quality) throws IOException {
        byte[] imageBytes = inputStream.readAllBytes();

        // Decode depuis les octets bruts
        Mat buf = new Mat(1, imageBytes.length, CV_8U);
        buf.data().put(imageBytes, 0, imageBytes.length);
        Mat img = imdecode(buf, IMREAD_COLOR);
        buf.release();

        if (img == null || img.empty()) {
            throw new IOException("Impossible de decoder l'image source.");
        }

        try {
            Mat processed = runPipeline(img);
            saveWebP(processed, target, Math.round(quality * 100));
            processed.release();
            log.info("Image traitee et sauvegardee -> {}", target);
        } finally {
            img.release();
        }
    }

    // =========================================================================
    // PIPELINE
    // =========================================================================

    private static Mat runPipeline(Mat original) {
        // Etape 1 : Detection contour + recadrage (Bounding Box)
        Mat cropped = detectAndCorrect(original);

        // On garde l'image telle quelle (en couleur) pour preserver les details.
        return cropped;
    }

    // =========================================================================
    // ETAPE 1 : DETECTION CONTOUR + PERSPECTIVE
    // =========================================================================

    private static Mat detectAndCorrect(Mat original) {
        // Niveaux de gris
        Mat gray = new Mat();
        cvtColor(original, gray, COLOR_BGR2GRAY);

        // Flou gaussien 
        Mat blurred = new Mat();
        GaussianBlur(gray, blurred, new Size(5, 5), 0);
        gray.release();

        // Binarisation Otsu (separe parfaitement le papier blanc du fond sombre)
        Mat thresh = new Mat();
        threshold(blurred, thresh, 0, 255, THRESH_BINARY + THRESH_OTSU);
        blurred.release();

        // Fermeture morphologique tres forte pour fusionner tout le contenu du ticket en un seul gros bloc solide
        Mat closeKernel = getStructuringElement(MORPH_RECT, new Size(21, 21));
        Mat closed = new Mat();
        morphologyEx(thresh, closed, MORPH_CLOSE, closeKernel);
        closeKernel.release();
        thresh.release();

        // Recherche des contours externes
        MatVector contours = new MatVector();
        Mat hierarchy = new Mat();
        findContours(closed, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
        closed.release();
        hierarchy.release();

        // Trouver le plus grand element (le ticket)
        Rect roi = findLargestBoundingRect(contours, original.rows(), original.cols());
        releaseAll(contours);

        if (roi != null) {
            log.info("Ticket detecte, recadrage simple (Bounding Box) en cours.");
            // Recadrage direct (sans deformation de perspective)
            return new Mat(original, roi).clone();
        }

        log.info("Aucun ticket detecte, image conservee telle quelle.");
        return original; // pas de copie, sera geree par l'appelant
    }

    /**
     * Cherche le plus grand contour et retourne son rectangle englobant droit (boundingRect).
     * Plus sur que la perspective pour eviter les distorsions.
     */
    private static Rect findLargestBoundingRect(MatVector contours, int imgH, int imgW) {
        int minW = imgW / 20; // 5% de la largeur
        int minH = imgH / 20; // 5% de la hauteur
        
        int minX = imgW, minY = imgH, maxX = 0, maxY = 0;
        boolean found = false;

        for (long i = 0; i < contours.size(); i++) {
            Mat c = contours.get(i);
            Rect r = boundingRect(c);
            
            // Ignorer les petites taches de bruit
            if (r.width() > minW || r.height() > minH) {
                if (r.x() < minX) minX = r.x();
                if (r.y() < minY) minY = r.y();
                if (r.x() + r.width() > maxX) maxX = r.x() + r.width();
                if (r.y() + r.height() > maxY) maxY = r.y() + r.height();
                found = true;
            }
        }

        if (!found) {
            return null;
        }

        // Ajouter une legere marge de securite (1% de l'image)
        int marginX = imgW / 100;
        int marginY = imgH / 100;
        
        minX = Math.max(0, minX - marginX);
        minY = Math.max(0, minY - marginY);
        maxX = Math.min(imgW, maxX + marginX);
        maxY = Math.min(imgH, maxY + marginY);

        return new Rect(minX, minY, maxX - minX, maxY - minY);
    }

    // =========================================================================
    // SAUVEGARDE WEBP
    // =========================================================================

    private static void saveWebP(Mat img, Path target, int quality) throws IOException {
        IntPointer params = new IntPointer(new int[]{ IMWRITE_WEBP_QUALITY, quality });
        BytePointer buffer = new BytePointer();
        
        // Encoder l'image en WebP en memoire
        boolean ok = imencode(".webp", img, buffer, params);
        if (!ok) {
            // Fallback JPEG si WebP non disponible
            log.warn("L'encodage WebP a echoue, fallback en JPEG.");
            params.put(0, IMWRITE_JPEG_QUALITY);
            ok = imencode(".jpg", img, buffer, params);
        }
        
        if (ok) {
            byte[] bytes = new byte[(int) buffer.limit()];
            buffer.get(bytes);
            Files.write(target, bytes);
        } else {
            buffer.close();
            params.close();
            throw new IOException("Erreur lors de l'encodage de l'image (ni WebP ni JPEG supporte).");
        }
        
        buffer.close();
        params.close();
    }

    // =========================================================================
    // UTILITAIRES
    // =========================================================================

    /** Ordonne 4 points (x0,y0,...,x3,y3) en [TL, TR, BR, BL]. */
    private static float[] orderPoints(float[] pts) {
        float[] sums  = new float[4];
        float[] diffs = new float[4];
        for (int i = 0; i < 4; i++) {
            sums[i]  = pts[i * 2] + pts[i * 2 + 1];
            diffs[i] = pts[i * 2] - pts[i * 2 + 1];
        }
        int tl = argMin(sums), br = argMax(sums);
        int tr = argMin(diffs), bl = argMax(diffs);
        return new float[]{
                pts[tl*2], pts[tl*2+1],
                pts[tr*2], pts[tr*2+1],
                pts[br*2], pts[br*2+1],
                pts[bl*2], pts[bl*2+1]
        };
    }

    /**
     * Distance euclidienne entre le point i et le point j dans un tableau
     * de points [x0,y0,x1,y1,...]. IMPORTANT : i et j sont des INDICES DE POINT
     * (0..3), pas des offsets dans le tableau — la fonction fait deja *2 en interne.
     */
    private static float euclidean(float[] pts, int i, int j) {
        float dx = pts[j * 2] - pts[i * 2];
        float dy = pts[j * 2 + 1] - pts[i * 2 + 1];
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /** Construit un Mat CV_32FC2 de 4 points (pour getPerspectiveTransform). */
    private static Mat buildPointMat(float x0, float y0,
                                     float x1, float y1,
                                     float x2, float y2,
                                     float x3, float y3) {
        Mat m = new Mat(4, 1, CV_32FC2);
        FloatIndexer fi = m.createIndexer();
        fi.put(0, 0L, 0L, x0); fi.put(0, 0L, 1L, y0);
        fi.put(1, 0L, 0L, x1); fi.put(1, 0L, 1L, y1);
        fi.put(2, 0L, 0L, x2); fi.put(2, 0L, 1L, y2);
        fi.put(3, 0L, 0L, x3); fi.put(3, 0L, 1L, y3);
        return m;
    }

    private static int argMin(float[] a) {
        int idx = 0;
        for (int i = 1; i < a.length; i++) if (a[i] < a[idx]) idx = i;
        return idx;
    }

    private static int argMax(float[] a) {
        int idx = 0;
        for (int i = 1; i < a.length; i++) if (a[i] > a[idx]) idx = i;
        return idx;
    }

    private static void releaseAll(MatVector mv) {
        for (long i = 0; i < mv.size(); i++) mv.get(i).release();
        // MatVector n'a pas de release() dans JavaCV 1.5.10, le GC s'en charge
    }
}