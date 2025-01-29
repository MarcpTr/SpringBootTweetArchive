package com.example.demo.service;

import com.example.demo.model.Collection;
import com.example.demo.model.Tweet;
import com.example.demo.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class TweetService {
    @Autowired
    private TweetRepository tweetRepository;

    public void addTweet(String tweetLink, Collection collection) {
        Optional<Tweet> tweetExistente= tweetRepository.findByTweetAndCollection(tweetLink, collection);
        if(tweetExistente.isPresent()){
            throw new RuntimeException("The tweet already exists in the collection.");
        }
        Tweet tweet = new Tweet();
        tweet.setTweet(tweetLink);
        tweet.setCollection(collection);
        tweet.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        tweetRepository.save(tweet);
    }
}
