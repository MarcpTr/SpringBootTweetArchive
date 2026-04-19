package com.tweetarchive.main.service;

import com.tweetarchive.main.exceptions.CollectionNotFoundException;
import com.tweetarchive.main.exceptions.InvalidCredentialsException;
import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.Tweet;
import com.tweetarchive.main.model.enums.AddTweetResult;
import com.tweetarchive.main.repository.CollectionRepository;
import com.tweetarchive.main.repository.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final CollectionRepository collectionRepository;

    public Optional<Tweet> findById(long id) {
        return tweetRepository.findById(id);
    }

    public AddTweetResult addTweetToCollection(long collectionId, String tweetLink) {
        Collection collection = collectionRepository
                .findByIdAndUserId(collectionId, getUserId())
                .orElseThrow(CollectionNotFoundException::new);

        if (tweetRepository.findByTweetAndCollectionId(tweetLink, collectionId).isPresent()) {
            return AddTweetResult.ALREADY_EXISTS;
        }

        Tweet tweet = new Tweet();
        tweet.setTweet(tweetLink);
        tweet.setCollection(collection);
        tweet.setCreatedAt(LocalDateTime.now());

        try {
            tweetRepository.save(tweet);
        } catch (DataIntegrityViolationException e) {
            return AddTweetResult.ALREADY_EXISTS;
        }
        return AddTweetResult.SUCCESS;
    }

    public void delete(Tweet tweet) {
        tweetRepository.delete(tweet);
    }

    private Long getUserId() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof CustomUserDetails user) {
            return user.getId();
        }

        throw new InvalidCredentialsException();
    }
}
