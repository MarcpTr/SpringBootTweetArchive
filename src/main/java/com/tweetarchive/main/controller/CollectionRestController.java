package com.tweetarchive.main.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetarchive.main.model.DTO.VisibilityRequest;
import com.tweetarchive.main.service.CollectionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/collection")
public class CollectionRestController {
    private final CollectionService collectionService;

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
}