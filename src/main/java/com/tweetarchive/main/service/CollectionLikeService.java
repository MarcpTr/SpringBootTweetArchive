package com.tweetarchive.main.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.CollectionLike;
import com.tweetarchive.main.model.User;
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
    public void like(Long userId, Long collectionId) {

        if (likeRepo.existsByUserIdAndCollectionId(userId, collectionId)) {
            return; 
        }

        User user = userRepo.findById(userId).orElseThrow();
        Collection collection = collectionRepo.findById(collectionId).orElseThrow();

        CollectionLike like = new CollectionLike();
        like.setUser(user);
        like.setCollection(collection);
        like.setCreatedAt(LocalDateTime.now());

        likeRepo.save(like);
    }

    @Transactional
    public void unlike(Long userId, Long collectionId) {
        likeRepo.deleteByUserIdAndCollectionId(userId, collectionId);
    }
}