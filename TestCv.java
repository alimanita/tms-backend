import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class TestCv {
    public static void main(String[] args) {
        String path = "C:/Users/HP/.gemini/antigravity/brain/c02dcaad-4f62-48c2-8b6e-afd728ad56fd/.user_uploaded/media_1787737630544.png";
        Mat original = imread(path);
        if(original.empty()) { System.out.println("Empty"); return; }
        
        Mat gray = new Mat();
        cvtColor(original, gray, COLOR_BGR2GRAY);
        Mat blurred = new Mat();
        GaussianBlur(gray, blurred, new Size(5, 5), 0);
        Mat thresh = new Mat();
        threshold(blurred, thresh, 0, 255, THRESH_BINARY + THRESH_OTSU);
        
        Mat closeKernel = getStructuringElement(MORPH_RECT, new Size(21, 21));
        Mat closed = new Mat();
        morphologyEx(thresh, closed, MORPH_CLOSE, closeKernel);
        
        MatVector contours = new MatVector();
        findContours(closed, contours, new Mat(), RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
        
        double imgH = original.rows();
        double imgW = original.cols();
        double minArea = imgH * imgW * 0.05;
        System.out.println("Total area: " + (imgH*imgW) + " Min area: " + minArea);
        
        for (long i = 0; i < contours.size(); i++) {
            double area = contourArea(contours.get(i));
            if(area > minArea) {
                System.out.println("Found contour with area: " + area);
                Rect roi = boundingRect(contours.get(i));
                System.out.println("ROI: " + roi.x() + ", " + roi.y() + " w=" + roi.width() + " h=" + roi.height());
            }
        }
    }
}
