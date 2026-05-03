package com.tweetarchive.main.service;

import com.tweetarchive.main.exceptions.AuthenticationException;
import com.tweetarchive.main.exceptions.ForbiddenOperationException;
import com.tweetarchive.main.exceptions.ResourceNotFoundException;
import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.Tweet;
import com.tweetarchive.main.model.User;
import com.tweetarchive.main.model.DTO.CollectionDTO;
import com.tweetarchive.main.model.DTO.CollectionPreviewDTO;
import com.tweetarchive.main.model.DTO.TweetDTO;
import com.tweetarchive.main.model.DTO.VisibilityResponse;
import com.tweetarchive.main.model.enums.ErrorCode;
import com.tweetarchive.main.repository.CollectionLikeRepository;
import com.tweetarchive.main.repository.CollectionRepository;
import com.tweetarchive.main.repository.TweetRepository;
import com.tweetarchive.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final CollectionLikeRepository collectionLikeRepository;

    public List<CollectionPreviewDTO> findBestCollections() {

        return collectionRepository.findTopCollections(
                getCurrentUserId(),
                PageRequest.of(0, 10));
    }

    public List<CollectionPreviewDTO> findUserCollections(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        List<Long> collectionIds = collectionRepository.findByIsPublicAndUserId(user.getId());

        return getPreviewByCollections(collectionIds, getCurrentUserId());
    }

    public List<CollectionPreviewDTO> findMyCollections() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            return List.of();
        }

        List<Long> collectionIds = collectionRepository.findAllIdsByUserId(userDetails.getId());
        return getPreviewByCollections(collectionIds, getCurrentUserId());
    }

    private List<CollectionPreviewDTO> getPreviewByCollections(List<Long> collectionsIds, Long userId) {
        if (collectionsIds.isEmpty()) {
            return List.of();
        }

        if (userId == null) {
            return collectionRepository.findByIdsWithPreviewTweetNoUser(collectionsIds);
        }

        return collectionRepository.findByIdsWithPreviewTweet(collectionsIds, userId);

    }

    public CollectionDTO viewCollection(long id) {

        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException(ErrorCode.COLLECTION_NOT_FOUND);
                });

        String currentUsername = getCurrentUsername();

        if (!collection.isPublic() &&
                !collection.getUser().getUsername().equals(currentUsername)) {
            throw new ResourceNotFoundException(ErrorCode.COLLECTION_NOT_FOUND);
        }

        CollectionDTO dto = new CollectionDTO();
        dto.setId(collection.getId());
        dto.setName(collection.getName());
        dto.setUsername(collection.getUser().getUsername());
        dto.setUserId(collection.getUser().getId());

        List<TweetDTO> tweets = tweetRepository.findAllByCollectionId(id);
        dto.setTweets(tweets);

        var likes = collection.getLikes();

        dto.setLikesCount(likes.size());

        dto.setLikedByCurrentUser(
                likes.stream()
                        .anyMatch(like -> like.getUser().getUsername().equals(currentUsername)));

        return dto;
    }

    @Transactional
    public VisibilityResponse changeVisibility(long collectionId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new ForbiddenOperationException("Unauthorized");
        }

        Long userId = user.getId();

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException(ErrorCode.COLLECTION_NOT_FOUND);
                });

        if (!collection.getUser().getId().equals(userId)) {
            throw new ForbiddenOperationException("You are not the owner of this collection");
        }

        collection.setPublic(!collection.isPublic());
        collection = collectionRepository.save(collection);
        return new VisibilityResponse(collection.getId(), collection.isPublic());
    }

    public Long createCollection(String collectionName, boolean isPublic) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof CustomUserDetails)) {
            throw new AuthenticationException(ErrorCode.AUTHENTICATED_ERROR);
        }

        Long userId = ((CustomUserDetails) principal).getId();

        Collection collection = new Collection();
        collection.setName(collectionName);
        collection.setPublic(isPublic);
        collection.setCreatedAt(LocalDateTime.now());
        collection.setLastVisitedAt(LocalDateTime.now());

        User user = userRepository.getReferenceById(userId);
        collection.setUser(user);

        collection = collectionRepository.save(collection);

        return collection.getId();
    }

    public boolean updateIsPublic(Collection collection) {
        collection.setPublic(!collection.isPublic());
        collectionRepository.save(collection);
        return true;
    }

    public void save(Collection collection) {
        collectionRepository.save(collection);
    }

    @Transactional
    public void deleteTweet(Long collectionId, Long tweetId) {
        Long userId = getCurrentUserId();
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException(ErrorCode.TWEET_DOESNT_EXIST);
                });

        if (!tweet.getCollection().getId().equals(collectionId)) {
            throw new ResourceNotFoundException(ErrorCode.TWEET_NOT_BELONG_THIS_COLLECTION);
        }

        if (!tweet.getCollection().getUser().getId().equals(userId)) {
            throw new AuthenticationException(ErrorCode.AUTHENTICATED_ERROR);
        }

        tweetRepository.delete(tweet);
    }

    @Transactional
    public void deleteCollection(Long collectionId) {
        Collection collection = collectionRepository
                .findByIdAndUserId(collectionId, getCurrentUserId()).orElseThrow(() -> {
                    return new ResourceNotFoundException(ErrorCode.COLLECTION_NOT_FOUND);
                });

        tweetRepository.deleteByCollectionId(collectionId);

        collectionLikeRepository.deleteByCollectionId(collectionId);

        collectionRepository.delete(collection);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {

            return null;
        }
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getId();
    }

    public Long getCurrentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getId();
    }

}
