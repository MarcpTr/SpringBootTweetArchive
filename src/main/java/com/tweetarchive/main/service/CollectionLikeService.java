package com.tweetarchive.main.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.tweetarchive.main.exceptions.AlreadyLikedException;
import com.tweetarchive.main.exceptions.ResourceNotFoundException;
import com.tweetarchive.main.exceptions.UserNotFoundException;
import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.CollectionLike;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.User;
import com.tweetarchive.main.model.DTO.LikeResponse;
import com.tweetarchive.main.repository.CollectionLikeRepository;
import com.tweetarchive.main.repository.CollectionRepository;
import com.tweetarchive.main.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectionLikeService {

    private final CollectionLikeRepository likeRepo;
    private final UserRepository userRepo;
    private final CollectionRepository collectionRepo;

    @Transactional
    public LikeResponse like(Long collectionId) {
        Map<String, String> errors = new HashMap<>();
        Collection collection = collectionRepo.findById(collectionId).orElseThrow(() -> {
            errors.put("COLLECTION", "LA coleccion no existe.");
            return new ResourceNotFoundException(errors);
        });
        long userId = getCurrentUserId();
        if (likeRepo.existsByUserIdAndCollectionId(userId, collectionId)) {
            errors.put("ALREADY LIKED", "El usuario ya ha dado like a este recurso.");
            throw new AlreadyLikedException(errors);
        }

        User user = userRepo.findById(userId).orElseThrow(() -> {
            return new UserNotFoundException("El usuario no existe");
        });

        CollectionLike like = new CollectionLike();
        like.setUser(user);
        like.setCollection(collection);
        like.setCreatedAt(LocalDateTime.now());

        like = likeRepo.save(like);
        return new LikeResponse(collectionId, userId, true);
    }

    @Transactional
    public void unlike(Long userId, Long collectionId) {
        likeRepo.deleteByUserIdAndCollectionId(userId, collectionId);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {

            return null;
        }
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getId();
    }

    public Long getCurrentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getId();
    }
}