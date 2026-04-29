package com.tweetarchive.main.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tweetarchive.main.model.TweetContent;

public interface TweetContentRepository extends JpaRepository<TweetContent, Long> {
        Optional<TweetContent> findByTweetId(String tweetId);
}
