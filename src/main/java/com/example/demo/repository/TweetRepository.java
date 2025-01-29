package com.example.demo.repository;

import com.example.demo.model.Collection;
import com.example.demo.model.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TweetRepository extends  JpaRepository<Tweet, Long>{
    Optional<Tweet> findByTweetAndCollection(String tweet, Collection collection);
}
