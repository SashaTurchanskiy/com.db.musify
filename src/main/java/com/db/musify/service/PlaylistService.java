package com.db.musify.service;

import com.db.musify.dto.request.PlaylistRequest;
import com.db.musify.dto.response.MessageResponse;
import com.db.musify.dto.response.PaginatedResponse;
import com.db.musify.dto.response.PlaylistResponse;
import com.db.musify.dto.response.PlaylistWithSongsResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface PlaylistService {

    PlaylistResponse createPlaylist(PlaylistRequest request, MultipartFile imageFile, String email);

    PlaylistResponse updatePlaylistPrivacy(Long id, Boolean isPublic, String email);

    MessageResponse addSongToPlaylist(Long playlistId, Long songId, String email);

    MessageResponse removeSongFromPlaylist(Long playlistId, Long songId, String email);

    MessageResponse reorderSongInPlaylist(Long playlistId, Long songId, Integer newPosition, String email);

    PaginatedResponse<PlaylistResponse> getAllPublicPlaylist(int page, int size, String search);

    PaginatedResponse<PlaylistResponse> getMyPlaylist(String email, int page, int size, String search);

    PlaylistWithSongsResponse getPlaylistWithSongs(Long playlistId, String email);

    MessageResponse deletePlaylist(Long playlistId, String email);
}
