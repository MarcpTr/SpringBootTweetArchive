package com.tweetarchive.main.service;

import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.User;
import com.tweetarchive.main.repository.CollectionRepository;
import com.tweetarchive.main.repository.TweetRepository;
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
    public Optional<List<Collection>> findCollectionsByName(String name) {
        return collectionRepository.searchByNameFuzzy("%" + name + "%");
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

    public Optional<List<Collection>> findByIsPublicandUserId(boolean isPublic, Long id) {
        return collectionRepository.findByIsPublicAndUserId(isPublic, id);
    }


}
