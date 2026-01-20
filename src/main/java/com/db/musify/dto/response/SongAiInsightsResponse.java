package com.db.musify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongAiInsightsResponse {

    private String analysis;
    private List<String> mods;
    private String genre;
    private Integer tempo;
    private String key;
    private Integer energy;
    private List<String> similarArtist;
    private String recommendedFor;
}
