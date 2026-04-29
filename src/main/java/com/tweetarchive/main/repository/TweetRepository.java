package com.tweetarchive.main.repository;

import com.tweetarchive.main.model.Tweet;
import com.tweetarchive.main.model.DTO.TweetDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    Optional<Tweet> findByTweetAndCollectionId(String tweet, long collectionId);

    @Query("""
                SELECT new com.tweetarchive.main.model.DTO.TweetDTO(
                    t.id,
                    t.tweet,
                    tc.embedHtml,
                    t.createdAt
                )
                FROM Tweet t
                LEFT JOIN t.content tc
                WHERE t.collection.id = :id
            """)
    List<TweetDTO> findAllByCollectionId(@Param("id") Long id);

    void deleteByCollectionId(long collectionId);

    Optional<Tweet> findById(Long tweetId);
}
