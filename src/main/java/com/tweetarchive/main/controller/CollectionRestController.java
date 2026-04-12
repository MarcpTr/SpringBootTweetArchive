package com.tweetarchive.main.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.User;
import com.tweetarchive.main.repository.CollectionRepository;
import com.tweetarchive.main.service.CollectionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/collection")
public class CollectionRestController {
    private final CollectionService collectionService;

    @PutMapping("/collection/{collectionId}/change-visibility")
    public ResponseEntity<Map<String, Object>> changeVisibility(@PathVariable Long collectionId) {

        Map<String, Object> response = collectionService.changeVisibility(collectionId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
}