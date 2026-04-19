package com.tweetarchive.main.repository;

import com.tweetarchive.main.model.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
   
    Optional<Tweet> findByTweetAndCollectionId(String tweet, long collectionId);
    Optional<List<Tweet>> findAllByCollectionId(Long id);
}
