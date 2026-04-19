package com.tweetarchive.main.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.Tweet;
import com.tweetarchive.main.repository.CollectionRepository;
import com.tweetarchive.main.service.CollectionService;
import com.tweetarchive.main.service.TweetService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/collection")
public class CollectionRestController {
    private final CollectionService collectionService;
    private final TweetService tweetService;
    private final CollectionRepository collectionRepository;

    @PutMapping("/{collectionId}/visibility")
    public ResponseEntity<?> changeVisibility(@PathVariable long collectionId) {

        collectionService.changeVisibility(collectionId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Visibility updated"));

    }

    @DeleteMapping("/{collectionId}")
    public ResponseEntity<?> deleteCollection(@PathVariable long collectionId) {

        collectionService.deleteCollection(collectionId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Collection deleted"));
    }

    @DeleteMapping("/{collectionId}/tweets/{tweetId}")
    public ResponseEntity<Map<String, Object>> removeTweetFromCollection(
            @PathVariable Long collectionId,
            @PathVariable Long tweetId,
            @AuthenticationPrincipal CustomUserDetails user) {
        System.out.println(user.getUsername());
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        // Verificar propietario
        if (!collection.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Tweet tweet = tweetService.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        // Verificar pertenencia
        if (!tweet.getCollection().getId().equals(collectionId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Tweet does not belong to this collection"));
        }

        tweetService.delete(tweet);

        response.put("status", "success");
        response.put("message", "Tweet removed from collection");

        return ResponseEntity.ok(response);
    }
}