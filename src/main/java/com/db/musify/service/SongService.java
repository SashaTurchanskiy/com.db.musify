package com.db.musify.service;

import com.db.musify.dto.request.SongRequest;
import com.db.musify.dto.response.SongResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SongService {

    SongResponse addSong(SongRequest request, MultipartFile songFile, MultipartFile imageFile, String email);
}
