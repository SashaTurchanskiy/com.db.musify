package com.db.musify.service.impl;

import com.db.musify.dto.request.SongRequest;
import com.db.musify.dto.response.SongResponse;
import com.db.musify.entity.AppUser;
import com.db.musify.entity.Song;
import com.db.musify.repository.AppUserRepository;
import com.db.musify.repository.PlaylistSongRepository;
import com.db.musify.repository.SongRepository;
import com.db.musify.service.SongService;
import com.db.musify.util.FileHandlerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final AppUserRepository appUserRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final FileHandlerUtil fileHandlerUtil;
    // GenericGeminiService

    @Value("${app.base.url}")
    private String basedUrl;

    @Override
    public SongResponse addSong(SongRequest request, MultipartFile songFile, MultipartFile imageFile, String email) {
        AppUser appUser = getUserByEmail(email);
        String uniqueId = UUID.randomUUID().toString();

        Song song = new Song();
        song.setAppUser(appUser);
        updateSongMetadata(song, request);

        String songUrl = processSongFile(songFile, uniqueId);
        song.setSongUrl(songUrl);

        String imageUrl = processImageFile(imageFile, uniqueId);
        song.setImageUrl(imageUrl);

        Song savedSong = songRepository.save(song);

        return SongResponse.fromEntity(savedSong, basedUrl);
    }

    private String processImageFile(MultipartFile imageFile, String uniqueId) {
        if (imageFile == null || imageFile.isEmpty()){
            return null;
        }
        String imageExtension = fileHandlerUtil.getFileExtension(imageFile.getOriginalFilename());
        String imageFileName = uniqueId + imageExtension;
        fileHandlerUtil.saveImageFileWithName(imageFile, imageFileName);
        return "/api/file/image/" + imageFileName;
    }

    private String processSongFile(MultipartFile songFile, String uniqueId) {
        String songExtension = fileHandlerUtil.getFileExtension(songFile.getOriginalFilename());
        String songFilename = uniqueId + songExtension;
        fileHandlerUtil.saveSongFileWithName(songFile, songFilename);
        return "/api/file/song/" + songFilename;
    }

    private void updateSongMetadata(Song song, SongRequest request) {
        song.setTitle(request.getTitle());
        song.setArtist(request.getArtist());
    }

    private AppUser getUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));
    }
}
