package com.example.demo.service;

import com.example.demo.model.Collection;
import com.example.demo.model.Tweet;
import com.example.demo.model.User;
import com.example.demo.repository.CollectionRepository;
import com.example.demo.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
@Service
public class CollectionService {
    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private TweetRepository tweetRepository;

    public Collection createCollection(String collectionName, User user){
        Collection collection= new Collection();
        collection.setName(collectionName);
        collection.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        collection.setLastVisitedAt(new Timestamp(System.currentTimeMillis()));
        collection.setUser(user);
        return collectionRepository.save(collection);
    }
    public void addTweetToCollection(String tweetLink, Collection collection) {
        Tweet tweet = new Tweet();
        tweet.setTweet(tweetLink);
        tweet.setCollection(collection);
        tweet.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        tweetRepository.save(tweet);
    }
    public void checkAndDeleteOldCollections() {
        long oneYearInMillis = 365 * 24 * 60 * 60 * 1000L;
        Timestamp oneYearAgo = new Timestamp(System.currentTimeMillis() - oneYearInMillis);
        List<Collection> oldCollections = collectionRepository.findByLastVisitedAtBefore(oneYearAgo);

        for (Collection collections : oldCollections) {
            collectionRepository.delete(collections);
        }
    }
}
