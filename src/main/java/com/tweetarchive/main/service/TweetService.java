package com.tweetarchive.main.service;

import com.tweetarchive.main.exceptions.CollectionNotFoundException;
import com.tweetarchive.main.exceptions.InvalidCredentialsException;
import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.Tweet;
import com.tweetarchive.main.model.TweetContent;
import com.tweetarchive.main.model.enums.AddTweetResult;
import com.tweetarchive.main.repository.CollectionRepository;
import com.tweetarchive.main.repository.TweetContentRepository;
import com.tweetarchive.main.repository.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final CollectionRepository collectionRepository;
    private final TweetContentRepository tweetContentRepository;
    
    public Optional<Tweet> findById(long id) {
        return tweetRepository.findById(id);
    }

    public AddTweetResult addTweetToCollection(long collectionId, String tweetLink) {

        Collection collection = collectionRepository
                .findByIdAndUserId(collectionId, getUserId())
                .orElseThrow(CollectionNotFoundException::new);

        // Evitar duplicado en la misma colección (tu lógica actual)
        if (tweetRepository.findByTweetAndCollectionId(tweetLink, collectionId).isPresent()) {
            return AddTweetResult.ALREADY_EXISTS;
        }

        String tweetId = extraerId(tweetLink);

        TweetContent content;

        try {
            content = tweetContentRepository.findByTweetId(tweetId)
                    .orElseGet(() -> {
                        TweetContent newContent = new TweetContent();
                        newContent.setTweetId(tweetId);
                        newContent.setUrl(tweetLink);
                        newContent.setCreatedAt(LocalDateTime.now());

                        String html = obtenerEmbedHtml(tweetLink);
                        newContent.setEmbedHtml(html);

                        return tweetContentRepository.save(newContent);
                    });

        } catch (DataIntegrityViolationException e) {
            // 🔥 Caso carrera: otro hilo lo creó justo antes
            content = tweetContentRepository.findByTweetId(tweetId)
                    .orElseThrow(() -> e);
        }

        // Crear el Tweet (tu entidad actual)
        Tweet tweet = new Tweet();
        tweet.setTweet(tweetLink);
        tweet.setCollection(collection);
        tweet.setContent(content);
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
private String extraerId(String url) {
    return url.substring(url.lastIndexOf("/") + 1);
}
    public String obtenerEmbedHtml(String tweetUrl) {
        String oembedUrl = "https://publish.twitter.com/oembed?url=" + tweetUrl;

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(oembedUrl, Map.class);

        return (String) response.get("html");
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
