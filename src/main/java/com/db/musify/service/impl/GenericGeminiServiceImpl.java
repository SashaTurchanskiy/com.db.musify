package com.db.musify.service.impl;


import com.db.musify.service.GenericGeminiService;

import com.google.genai.Client;
import com.google.genai.errors.ClientException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class GenericGeminiServiceImpl implements GenericGeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GenericGeminiServiceImpl.class);

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.models}")
    private String geminiModels;

    private final ObjectMapper objectMapper;

    @Override
    public <T> T generateContent(String prompt, Class<T> responseType) {
        if (prompt == null || prompt.trim().isEmpty()){
            throw new IllegalArgumentException("Prompt cannot be null or empty");
        }

        Client client = new com.google.genai.Client.Builder()
                .apiKey(geminiApiKey)
                .build();

        String[] models = geminiModels.split(",");
        Exception lastException = null;

        for (int i = 0; i < models.length; i++){
            try {
                logger.info("Calling Gemini Api with model: {} ({}/{})", models[i].trim(), i+1, models.length);
                String response = client.models.generateContent(models[i].trim(), prompt, null ).text();

                if (response == null || response.isEmpty()){
                    throw new RuntimeException("Empty response from Gemini API");
                }
            }catch (ClientException ex){
                if (ex.getMessage() != null && ex.getMessage().contains("429")){
                    logger.warn("Rate limit exceeded for {}. Trying next model...", models[i].trim());
                    lastException = ex;
                    if (i < models.length-1) continue;
                }else {
                    throw new RuntimeException("Gemini API error: " + ex.getMessage(), ex);
                }
            }
        }
        return null;
    }
}
