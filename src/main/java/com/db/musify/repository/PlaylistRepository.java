package com.db.musify.repository;

import com.db.musify.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    @Query("SELECT DISTINCT p FROM Playlist p JOIN PlaylistSong ps ON p.id = ps.playlist.id WHERE p.isPublic = true AND (LOWER(p.name) LIKE " +
            " LOWER(CONCAT('&', :search, '&')) OR  LOWER(p.description) LIKE  LOWER(concat('&', :search, '&')))")
    Page<Playlist> findPublicPlaylistWithSongsByNameOrDescription(String trim, Pageable pageable);

    @Query("SELECT  DISTINCT  p FROM Playlist p JOIN PlaylistSong ps ON p.id = ps.playlist.id WHERE  p.isPublic = true ")
    Page<Playlist> findPublicPlaylistWithSongs(Pageable pageable);
}
