package com.db.musify.service.impl;

import com.db.musify.dto.request.PlaylistRequest;
import com.db.musify.dto.response.MessageResponse;
import com.db.musify.dto.response.PaginatedResponse;
import com.db.musify.dto.response.PlaylistResponse;
import com.db.musify.entity.AppUser;
import com.db.musify.entity.Playlist;
import com.db.musify.entity.PlaylistSong;
import com.db.musify.entity.Song;
import com.db.musify.repository.AppUserRepository;
import com.db.musify.repository.PlaylistRepository;
import com.db.musify.repository.PlaylistSongRepository;
import com.db.musify.repository.SongRepository;
import com.db.musify.service.PlaylistService;
import com.db.musify.util.FileHandlerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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

    @Override
    public PlaylistResponse updatePlaylistPrivacy(Long id, Boolean isPublic, String email) {
        Playlist playlist = validatePlaylistAccess(id, email);

        playlist.setIsPublic(isPublic);

        Playlist updatedPlaylist = playlistRepository.save(playlist);

        return PlaylistResponse.fromEntity(updatedPlaylist, baseUrl);
    }

    @Override
    public MessageResponse addSongToPlaylist(Long playlistId, Long songId, String email) {
        Playlist playlist = validatePlaylistAccess(playlistId, email);

        Song song = songRepository.findById(songId)
                .orElseThrow(()-> new RuntimeException("Song not found"));

        if (playlistSongRepository.existsByPlaylistIdAndSongId(playlistId, songId)){
            throw new RuntimeException("Song already exists in playlist");
        }

        List<PlaylistSong> existingSong = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
        int nextPosition = existingSong.isEmpty() ? 1 : existingSong.get(existingSong.size()-1).getPosition()+1;

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylist(playlist);
        playlistSong.setSong(song);
        playlistSong.setPosition(nextPosition);

        playlistSongRepository.save(playlistSong);
        return new MessageResponse("Song added to playlist successfully");
    }

    @Override
    public MessageResponse removeSongFromPlaylist(Long playlistId, Long songId, String email) {
        validatePlaylistAccess(playlistId, email);

        PlaylistSong playlistSong = playlistSongRepository.findByPlaylistIdAndSongId(playlistId, songId)
                .orElseThrow(()-> new RuntimeException("Song not found is playlist"));

        int removedPosition = playlistSong.getPosition();

        playlistSongRepository.delete(playlistSong);

        List<PlaylistSong> songAfterRemoved = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
        for (PlaylistSong song : songAfterRemoved){
            if (song.getPosition() > removedPosition){
                song.setPosition(song.getPosition()-1);
                playlistSongRepository.save(song);
            }
        }
        return new MessageResponse("Song removed from playlist successfully");
    }

    @Override
    public MessageResponse reorderSongInPlaylist(Long playlistId, Long songId, Integer newPosition, String email) {
        validatePlaylistAccess(playlistId, email);

        PlaylistSong playlistSong = playlistSongRepository.findByPlaylistIdAndSongId(playlistId, songId)
                .orElseThrow(()-> new RuntimeException("Song not found in playlist"));

        List<PlaylistSong> allSongs = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);

        if (newPosition < 1 || newPosition > allSongs.size()){
            throw new RuntimeException("Invalid position. Must be between 1 and " + allSongs.size());
        }

        int currentPosition = playlistSong.getPosition();

        if (currentPosition == newPosition){
            return new MessageResponse("Song is already at position " + newPosition);
        }

        if (newPosition > currentPosition){
            for (PlaylistSong song : allSongs){
                if (song.getPosition() > currentPosition && song.getPosition() <= newPosition){
                    song.setPosition(song.getPosition() -1);
                    playlistSongRepository.save(song);
                }
            }
        }else {
            for (PlaylistSong song : allSongs){
                if (song.getPosition() >= newPosition && song.getPosition() < currentPosition){
                    song.setPosition(song.getPosition() + 1);
                    playlistSongRepository.save(song);
                }
            }
        }

        playlistSong.setPosition(newPosition);
        playlistSongRepository.save(playlistSong);

        List<PlaylistSong> finalSongs = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
        int normalizedPosition = 1;
        for (PlaylistSong song : finalSongs){
            if (song.getPosition() != normalizedPosition){
                song.setPosition(normalizedPosition);
                playlistSongRepository.save(song);
            }
            normalizedPosition++;
        }
        return new MessageResponse("Song reorder successfully to position " + newPosition);
    }

    @Override
    public PaginatedResponse<PlaylistResponse> getAllPublicPlaylist(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Playlist> playlistPage;

        if (search != null &&  !search.trim().isEmpty()){
            playlistPage = playlistRepository.findPublicPlaylistWithSongsByNameOrDescription(search.trim(), pageable);
        }else {
            playlistPage = playlistRepository.findPublicPlaylistWithSongs(pageable);
        }

        List<PlaylistResponse> playlistResponses = playlistPage.getContent().stream()
                .map(playlist -> PlaylistResponse.fromEntity(playlist, baseUrl))
                .toList();

        return new PaginatedResponse<>(
                playlistResponses,
                playlistPage.getNumber(),
                playlistPage.getSize(),
                playlistPage.getTotalElements(),
                playlistPage.getTotalPages(),
                playlistPage.isLast(),
                playlistPage.isFirst()
        );
    }

    private AppUser getUserByEmail(String email){
        return appUserRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));
    }
    private Playlist validatePlaylistAccess(Long id, String email){
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Playlist not found"));

        AppUser appUser = getUserByEmail(email);

        boolean isOwner = playlist.getAppUser().getId().equals(appUser.getId());
        boolean isAdmin = "ADMIN".equals(appUser.getRole());

        if (!isOwner && !isAdmin){
            throw new RuntimeException("You don`t have permission to modify this playlist");
        }
        return playlist;
    }
}
