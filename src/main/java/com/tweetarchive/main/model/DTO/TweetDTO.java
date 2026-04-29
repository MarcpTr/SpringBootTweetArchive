package com.tweetarchive.main.model.DTO;

import java.time.LocalDateTime;

public record TweetDTO(
        Long id,
        String tweetUrl,
        String embedHtml,
        LocalDateTime createdAt
) {}