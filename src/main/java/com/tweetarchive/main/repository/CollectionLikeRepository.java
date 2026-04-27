package com.tweetarchive.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tweetarchive.main.model.CollectionLike;

public interface CollectionLikeRepository extends JpaRepository<CollectionLike, Long> {

    boolean existsByUserIdAndCollectionId(Long userId, Long collectionId);

    void deleteByUserIdAndCollectionId(Long userId, Long collectionId);

    long countByCollectionId(Long collectionId);

    List<CollectionLike> findByUserId(Long userId);

    void deleteByCollectionId(Long collectionId);
}