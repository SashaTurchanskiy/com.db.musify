package com.db.musify.service;

import com.db.musify.dto.request.SongRequest;
import com.db.musify.dto.response.MessageResponse;
import com.db.musify.dto.response.SongResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface SongService {

    SongResponse addSong(SongRequest request, MultipartFile songFile, MultipartFile imageFile, String email);

    Object getAllSongs(Long userId, int page, int size, String search);

    SongResponse getSongById(Long id);

    SongResponse updateSong(Long id, SongRequest request, MultipartFile songFile, MultipartFile imageFile, String email);

    MessageResponse deleteSong(Long id, String email);
}
