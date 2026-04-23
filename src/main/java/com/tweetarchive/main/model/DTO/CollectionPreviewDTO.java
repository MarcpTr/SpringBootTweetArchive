package com.tweetarchive.main.model.DTO;

public record CollectionPreviewDTO(
        String username,
        Long userId,
        Long collectionId,
        String collectionName,
        boolean isPublic,
        String previewTweet,
        Long tweetCount,
        Long likeCount,
        boolean likedByCurrentUser) {
}