package com.tweetarchive.main.model.DTO;

import java.time.LocalDateTime;

public record TweetDTO(Long id,
        String text,
        LocalDateTime createdAt) {
}
