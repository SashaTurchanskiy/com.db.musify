package com.db.musify.service.impl;

import com.db.musify.dto.request.PlaylistRequest;
import com.db.musify.dto.response.PlaylistResponse;
import com.db.musify.entity.AppUser;
import com.db.musify.entity.Playlist;
import com.db.musify.repository.AppUserRepository;
import com.db.musify.repository.PlaylistRepository;
import com.db.musify.repository.PlaylistSongRepository;
import com.db.musify.repository.SongRepository;
import com.db.musify.service.PlaylistService;
import com.db.musify.util.FileHandlerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final AppUserRepository appUserRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongRepository songRepository;
    private final FileHandlerUtil fileHandlerUtil;


    @Value("${app.base.url}")
    private String baseUrl;


    @Override
    public PlaylistResponse createPlaylist(PlaylistRequest request, MultipartFile imageFile, String email) {
        AppUser appUser = getUserByEmail(email);

        Playlist playlist = new Playlist();
        playlist.setName(request.getName());
        playlist.setDescription(request.getDescription());
        playlist.setIsPublic(request.getIsPublic());
        playlist.setAppUser(appUser);

        if (imageFile != null && !imageFile.isEmpty()){
            String uniqueId = UUID.randomUUID().toString();
            String imageExtension = fileHandlerUtil.getFileExtension(imageFile.getOriginalFilename());
            String imageFilename = uniqueId = imageExtension;
            fileHandlerUtil.saveImageFileWithName(imageFile, imageFilename);
            playlist.setImageUrl("api/file/image" + imageFilename);
        }

        Playlist savedPlaylist = playlistRepository.save(playlist);

        return PlaylistResponse.fromEntity(savedPlaylist, baseUrl);
    }
    private AppUser getUserByEmail(String email){
        return appUserRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));
    }
}
