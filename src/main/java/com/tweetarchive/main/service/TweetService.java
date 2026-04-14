package com.tweetarchive.main.service;

import com.tweetarchive.main.exceptions.CollectionNotFoundException;
import com.tweetarchive.main.exceptions.FieldValidationException;
import com.tweetarchive.main.exceptions.InvalidCredentialsException;
import com.tweetarchive.main.exceptions.ResourceAlreadyExistsException;
import com.tweetarchive.main.exceptions.ResourceNotFoundException;
import com.tweetarchive.main.exceptions.TweetAlreadyExistsException;
import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.Tweet;
import com.tweetarchive.main.repository.CollectionRepository;
import com.tweetarchive.main.repository.TweetRepository;
import com.tweetarchive.main.util.TweetLinkValidator;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final CollectionRepository collectionRepository;

    public Optional<Tweet> findById(long id) {
        return tweetRepository.findById(id);
    }

    public long addTweetToCollection(long collectionId, String tweetLink) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof CustomUserDetails)) {
            throw new InvalidCredentialsException();
        }
        Optional<Collection> collection = collectionRepository.findByIdAndUserId(collectionId,
                ((CustomUserDetails) principal).getId());
        if (collection.isEmpty()) {
            throw new CollectionNotFoundException();
        }
        if (!TweetLinkValidator.isTweetUrl(tweetLink)) {
            throw new FieldValidationException();
        }
        /********************/
        Optional<Tweet> tweetExistente = tweetRepository.findByTweetAndCollectionId(tweetLink, collectionId);
        if (tweetExistente.isPresent()) {
            throw new TweetAlreadyExistsException(collectionId);
        }
        Tweet tweet = new Tweet();
        tweet.setTweet(tweetLink);
        tweet.setCollection(collectionRepository.getReferenceById(collectionId));
        tweet.setCreatedAt(LocalDateTime.now());
        tweetRepository.save(tweet);
        return collectionId;
    }

    public void delete(Tweet tweet) {
        tweetRepository.delete(tweet);
    }
}
