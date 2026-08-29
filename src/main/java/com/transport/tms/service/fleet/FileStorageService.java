package com.transport.tms.service.fleet;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${app.upload.fuel-proofs-dir:uploads/fuel-proofs}")
    private String uploadDir;

    public String store(MultipartFile file) {
        return store(file, "");
    }

    public String store(MultipartFile file, String subDir) {
        try {
            log.info("=== store file ===");
            log.info("uploadDir: {}/{}", uploadDir, subDir);
            log.info("originalName: {}", file.getOriginalFilename());
            log.info("size: {} bytes", file.getSize());
            log.info("contentType: {}", file.getContentType());

            if (file.isEmpty()) {
                log.warn("Le fichier est vide");
                return null;
            }

            Path dirPath = Paths.get(uploadDir, subDir);
            Files.createDirectories(dirPath);
            log.info("Dossier cree/verifie: {}", dirPath.toAbsolutePath());

            // Verifier les permissions d'ecriture
            if (!Files.isWritable(dirPath)) {
                log.error("Le dossier n'est pas accessible en ecriture: {}", dirPath.toAbsolutePath());
                throw new IOException("Le dossier n'est pas accessible en ecriture: " + dirPath.toAbsolutePath());
            }

            String contentType = file.getContentType();
            boolean isImage = contentType != null && contentType.startsWith("image/");

            if (isImage) {
                // Compression en WebP qualite 75
                String filename = UUID.randomUUID() + ".webp";
                Path target = dirPath.resolve(filename);
                log.info("Compression WebP -> {}", target.toAbsolutePath());
                ImageCompressionUtil.compressToWebP(file.getInputStream(), target, 0.75f);
                log.info("Image compressee en WebP avec succes: {}", target.toAbsolutePath());
                return filename;
            } else {
                // PDF ou autre -> stockage direct sans transformation
                String originalName = file.getOriginalFilename();
                String ext = originalName != null && originalName.contains(".")
                        ? originalName.substring(originalName.lastIndexOf('.') + 1)
                        : null;
                log.info("Extension detectee: {}", ext);
                String filename = UUID.randomUUID() + (ext != null && !ext.isBlank() ? "." + ext : "");
                log.info("Nom de fichier genere: {}", filename);
                Path target = dirPath.resolve(filename);
                log.info("Chemin cible: {}", target.toAbsolutePath());
                Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                log.info("Fichier enregistre avec succes: {}", target.toAbsolutePath());
                return filename;
            }

        } catch (IOException e) {
            log.error("Erreur lors de l'enregistrement du fichier", e);
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier: " + e.getMessage(), e);
        }
    }

    public Resource load(String filename) {
        return load(filename, "");
    }

    public Resource load(String filename, String subDir) {
        try {
            log.info("=== load file ===");
            log.info("filename: {}", filename);
            log.info("uploadDir: {}", uploadDir);

            if (filename == null || filename.isBlank()) {
                log.warn("Nom de fichier null ou vide");
                throw new EntityNotFoundException("Nom de fichier invalide");
            }

            Path file = Paths.get(uploadDir, subDir).resolve(filename);
            log.info("Chemin resolu: {}", file.toAbsolutePath());

            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists()) {
                log.error("Le fichier n'existe pas: {}", file.toAbsolutePath());
                throw new EntityNotFoundException("Fichier introuvable : " + filename);
            }

            if (!resource.isReadable()) {
                log.error("Le fichier n'est pas lisible: {}", file.toAbsolutePath());
                throw new EntityNotFoundException("Fichier non lisible : " + filename);
            }

            log.info("Fichier charge avec succes: {}", file.toAbsolutePath());
            return resource;
        } catch (MalformedURLException e) {
            log.error("URL mal formee pour le fichier: {}", filename, e);
            throw new RuntimeException("URL mal formee pour le fichier: " + filename, e);
        }
    }

    public void delete(String filename) {
        delete(filename, "");
    }

    public void delete(String filename, String subDir) {
        if (filename == null) {
            return;
        }

        try {
            log.info("=== delete file ===");
            log.info("filename: {}", filename);

            Path file = Paths.get(uploadDir, subDir).resolve(filename);
            boolean deleted = Files.deleteIfExists(file);
            log.info("Fichier supprime: {}", deleted);
        } catch (IOException e) {
            log.error("Erreur lors de la suppression du fichier: {}", filename, e);
        }
    }
}