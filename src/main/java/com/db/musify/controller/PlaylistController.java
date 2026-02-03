package com.db.musify.controller;

import com.db.musify.dto.request.PlaylistRequest;
import com.db.musify.dto.response.PlaylistResponse;
import com.db.musify.service.PlaylistService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/playlist")
@Validated
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping("/createPlaylist")
    public ResponseEntity<PlaylistResponse> createPlaylist(@RequestParam("name") @NotBlank(message = "Playlist name is required") @Size(max = 100, message = "Playlist name must not exceed 100 characters")
                                                               String name,
                                                            @RequestParam(value = "description", required = false) @Size(max = 500, message = "Description must not exceed 500 characters")
                                                            String description,
                                                            @RequestParam(value = "isPublic", defaultValue = "false")
                                                               Boolean isPublic,
                                                            @RequestParam(value = "imageFile", required = true) MultipartFile imageFile,
                                                           Authentication authentication){

        String email = authentication.getName();
        PlaylistRequest request = new PlaylistRequest(name, description, isPublic);
        PlaylistResponse response = playlistService.createPlaylist(request, imageFile, email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
