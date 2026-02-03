package com.db.musify.service;

import com.db.musify.dto.request.PlaylistRequest;
import com.db.musify.dto.response.PlaylistResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PlaylistService {

    PlaylistResponse createPlaylist(PlaylistRequest request, MultipartFile imageFile, String email);
}
