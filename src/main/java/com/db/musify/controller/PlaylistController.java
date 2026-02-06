package com.db.musify.controller;

import com.db.musify.dto.request.PlaylistRequest;
import com.db.musify.dto.response.MessageResponse;
import com.db.musify.dto.response.PaginatedResponse;
import com.db.musify.dto.response.PlaylistResponse;
import com.db.musify.dto.response.PlaylistWithSongsResponse;
import com.db.musify.service.PlaylistService;
import jakarta.mail.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

    @PatchMapping("/updatePlaylistPrivacy/{id}")
    public ResponseEntity<PlaylistResponse> updatePlaylistPrivacy(@PathVariable Long id,
                                                                  @RequestParam("isPublic") Boolean isPublic,
                                                                  Authentication authentication){

        String email = authentication.getName();

        PlaylistResponse response = playlistService.updatePlaylistPrivacy(id, isPublic, email);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/addSongToPlaylist/{playlistId}")
    public ResponseEntity<MessageResponse> addSongToPlaylist(@PathVariable Long playlistId,
                                                             @RequestParam("songId") Long songId,
                                                             Authentication authentication){

        String email = authentication.getName();
        MessageResponse response = playlistService.addSongToPlaylist(playlistId, songId, email);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/removeSongFromPlaylist/{playlistId}")
    public ResponseEntity<MessageResponse> removeSongFromPlaylist(@PathVariable Long playlistId,
                                                                  @RequestParam("songId") Long songId,
                                                                  Authentication authentication){

        String email = authentication.getName();
        MessageResponse response = playlistService.removeSongFromPlaylist(playlistId, songId, email);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/reorderSongInPlaylist/{playlistId}")
    public ResponseEntity<MessageResponse> reorderSongInPlaylist(
            @PathVariable Long playlistId,
            @RequestParam("songId") Long songId,
            @RequestParam("newPosition") Integer newPosition,
            Authentication authentication){

        String email = authentication.getName();
        MessageResponse response = playlistService.reorderSongInPlaylist(playlistId, songId, newPosition, email);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getAllPublicPlaylist")
    public ResponseEntity<?> getAllPublicPlaylist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search){

        return ResponseEntity.ok(playlistService.getAllPublicPlaylist(page, size, search));
    }
    @GetMapping("/getMyPlaylist")
    public ResponseEntity<?> getMyPlaylist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Authentication authentication){

        if (authentication == null){
            return ResponseEntity.status(401).body("Authentication required");
        }

        String email = authentication.getName();
        PaginatedResponse<PlaylistResponse> result = playlistService.getMyPlaylist(email, page, size, search);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/getPlaylistWithSongs/{playlistId}")
    public ResponseEntity<PlaylistWithSongsResponse> getPlaylistSongs(
            @PathVariable Long playlistId,
            Authentication authentication){

        String email = authentication.getName();
        PlaylistWithSongsResponse response = playlistService.getPlaylistWithSongs(playlistId, email);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/deletePlaylist/{playlistId}")
    public ResponseEntity<MessageResponse> deletePlaylist(
            @PathVariable Long playlistId,
            Authentication authentication){

        String email = authentication.getName();
        MessageResponse response = playlistService.deletePlaylist(playlistId, email);
        return ResponseEntity.ok(response);
    }

}
