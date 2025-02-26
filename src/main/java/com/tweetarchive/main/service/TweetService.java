package com.tweetarchive.main.service;

import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.Tweet;
import com.tweetarchive.main.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class TweetService {
    @Autowired
    private TweetRepository tweetRepository;
    public Optional<Tweet> findById(long id){
        return tweetRepository.findById(id);
    }
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

    public void delete(Tweet tweet) {
        tweetRepository.delete(tweet);
    }
}
