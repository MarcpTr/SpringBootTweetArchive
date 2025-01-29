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
import java.util.Optional;

@Service
public class CollectionService {
    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private TweetRepository tweetRepository;

    public Collection createCollection(String collectionName, boolean isPublic, User user){
        Collection collection= new Collection();
        collection.setName(collectionName);
        collection.setPublic(isPublic);
        collection.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        collection.setLastVisitedAt(new Timestamp(System.currentTimeMillis()));
        collection.setUser(user);
        return collectionRepository.save(collection);
    }

    public boolean updateIsPublic(Collection collection) {
            collection.setPublic(!collection.isPublic());
            collectionRepository.save(collection);
            return true;
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
