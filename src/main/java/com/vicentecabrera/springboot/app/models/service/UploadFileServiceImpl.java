package com.vicentecabrera.springboot.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImpl implements IUploadFileService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String UPLOADS_FOLDER = "uploads";

    public UploadFileServiceImpl() {
    }

    public Resource load(String filename) throws MalformedURLException {
        Path pathFoto = getPath(filename);
        this.log.info("pathFoto: " + pathFoto);
        Resource recurso = new UrlResource(pathFoto.toUri());
        if (recurso.exists() && recurso.isReadable()) {
            return recurso;
        } else {
            throw new RuntimeException("Error: no se puede cargar la imagen: " + pathFoto.toString());
        }
    }

    public String copy(MultipartFile file) throws IOException {
        String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path rootPath = getPath(uniqueFilename);
        log.info("rootPath: " + rootPath);
        Files.copy(file.getInputStream(), rootPath, new CopyOption[0]);
        return uniqueFilename;
    }

    public boolean delete(String filename) {
        Path rootPath = getPath(filename);
        File archivo = rootPath.toFile();
        return archivo.exists() && archivo.canRead() && archivo.delete();
    }

    public Path getPath(String filename) {
        return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
    }

   
}
