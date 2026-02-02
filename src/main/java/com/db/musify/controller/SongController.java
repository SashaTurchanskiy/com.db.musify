package com.db.musify.controller;

import com.db.musify.dto.response.SongAiInsightsResponse;
import com.db.musify.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/song")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @GetMapping("/getSongAiInsights/{songId}")
    public ResponseEntity<SongAiInsightsResponse> getSongAiInsights(@PathVariable Long songId){
        SongAiInsightsResponse response = songService.getSongAiInsights(songId);
        return ResponseEntity.ok(response);
    }
}
