package com.db.musify.controller;

import com.db.musify.util.FileHandlerUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FIleController {

    private final FileHandlerUtil fileHandlerUtil;

    @GetMapping("/song/{filename}")
    public ResponseEntity<?> getSong(@PathVariable String filename){
        try {
            Resource resource = fileHandlerUtil.loadSongFile(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=\"" + filename + "\"")
                    .body(resource);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"File not found\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }
    @GetMapping("/image/{filename}")
    public ResponseEntity<?> getImage(@PathVariable String fileName){
        try {
            Resource resource = fileHandlerUtil.loadSongFile(fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"File not found\", \"message\": \"" + e.getMessage() + "\"}");

        }
    }
}
