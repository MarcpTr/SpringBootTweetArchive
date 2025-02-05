package com.tweetarchive.main.repository;

import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TweetRepository extends  JpaRepository<Tweet, Long>{
    Optional<Tweet> findByTweetAndCollection(String tweet, Collection collection);
}
