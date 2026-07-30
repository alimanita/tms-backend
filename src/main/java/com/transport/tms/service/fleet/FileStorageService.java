package com.transport.tms.service.fleet;


import jakarta.persistence.EntityNotFoundException;
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

@Service
public class FileStorageService {

    @Value("${app.upload.fuel-proofs-dir:uploads/fuel-proofs}")
    private String uploadDir;

    public String store(MultipartFile file) {
        try {
            Path dirPath = Paths.get(uploadDir);
            Files.createDirectories(dirPath);

            String originalName = file.getOriginalFilename();
            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf('.') + 1)
                    : null;

            String filename = UUID.randomUUID() + (ext != null && !ext.isBlank() ? "." + ext : "");

            Path target = dirPath.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier", e);
        }
    }
    public Resource load(String filename) {
        try {
            Path file = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) return resource;
            throw new EntityNotFoundException("Fichier introuvable : " + filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String filename) {
        if (filename == null) return;
        try {
            Files.deleteIfExists(Paths.get(uploadDir).resolve(filename));
        } catch (IOException ignored) {}
    }
}