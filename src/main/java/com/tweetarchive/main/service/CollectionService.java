package com.tweetarchive.main.service;

import com.tweetarchive.main.exceptions.CollectionNotFoundException;
import com.tweetarchive.main.exceptions.ForbiddenOperationException;
import com.tweetarchive.main.exceptions.InvalidCredentialsException;
import com.tweetarchive.main.exceptions.UserNotFoundException;
import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.User;
import com.tweetarchive.main.model.DTO.CollectionDTO;
import com.tweetarchive.main.model.DTO.CollectionPreviewDTO;
import com.tweetarchive.main.repository.CollectionRepository;
import com.tweetarchive.main.repository.TweetRepository;
import com.tweetarchive.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public List<CollectionPreviewDTO> findPublicCollections(boolean visibility) {
        List<Collection> publicCollections = collectionRepository.findByIsPublic(visibility);
        List<Long> ids = publicCollections.stream().map(Collection::getId).toList();
        List<CollectionPreviewDTO> collectionPreviewDTO = collectionRepository.findByIdsWithPreviewTweet(ids);
        return collectionPreviewDTO;
    }

    public List<CollectionPreviewDTO> findMyCollections() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId;
        if (principal instanceof CustomUserDetails) {
            userId = ((CustomUserDetails) principal).getId();
            List<CollectionPreviewDTO> collectionPreviewDTO = collectionRepository
                    .findByIdsWithPreviewTweet(collectionRepository.findAllByUserId(userId)
                            .stream()
                            .map(Collection::getId)
                            .toList());
            return collectionPreviewDTO;
        }
        return null;
    }

    public List<CollectionPreviewDTO> findUserCollections(String username) {

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(username);
        } else {
            long userId = user.get().getId();

            List<Collection> userCollections = collectionRepository.findByIsPublicAndUserId(true, userId);
            List<Long> ids = userCollections.stream().map(Collection::getId).toList();
            List<CollectionPreviewDTO> collectionPreviewDTO = collectionRepository.findByIdsWithPreviewTweet(ids);
            return collectionPreviewDTO;
        }
    }

    public CollectionDTO viewCollection(long id) {

        Collection collection = collectionRepository.findById(id)
                .orElseThrow(CollectionNotFoundException::new);

        String currentUsername = getCurrentUsername();

        // Validación de acceso
        if (!collection.isPublic() &&
                !collection.getUser().getUsername().equals(currentUsername)) {
            throw new CollectionNotFoundException();
        }

        CollectionDTO dto = new CollectionDTO();
        dto.setId(collection.getId());
        dto.setName(collection.getName());
        dto.setUsername(collection.getUser().getUsername());
        dto.setUserId(collection.getUser().getId());

        tweetRepository.findAllByCollectionId(id)
                .ifPresent(dto::setTweets);

        return dto;
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

    @Transactional
    public void changeVisibility(long collectionId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new ForbiddenOperationException("Unauthorized");
        }

        Long userId = user.getId();

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(CollectionNotFoundException::new);

        if (!collection.getUser().getId().equals(userId)) {
            throw new ForbiddenOperationException("You are not the owner of this collection");
        }

        collection.setPublic(!collection.isPublic());
    }

    public Long createCollection(String collectionName, boolean isPublic) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof CustomUserDetails)) {
            throw new InvalidCredentialsException();
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
    /*
     * 
     * public Optional<List<Collection>> findByUserId(long id) {
     * return collectionRepository.findByUserId(id);
     * }
     */
    /*
     * public Optional<List<Collection>> findCollectionsByName(String name) {
     * return collectionRepository.searchByNameFuzzy("%" + name + "%");
     * }
     */

    public boolean updateIsPublic(Collection collection) {
        collection.setPublic(!collection.isPublic());
        collectionRepository.save(collection);
        return true;
    }

    public void save(Collection collection) {
        collectionRepository.save(collection);
    }

    /*
     * public void checkAndDeleteOldCollections() {
     * LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
     * List<Collection> oldCollections =
     * collectionRepository.findByLastVisitedAtBefore(oneYearAgo);
     * 
     * for (Collection collections : oldCollections) {
     * collectionRepository.delete(collections);
     * }
     * }
     */

    /*
     * public Optional<List<Collection>> searchByNameFuzzy(String searchQuery) {
     * return collectionRepository.searchByNameFuzzy(searchQuery);
     * }
     */

    public void delete(Collection collection) {
        collectionRepository.delete(collection);
    }

    @Transactional
    public void deleteCollection(long collectionId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new ForbiddenOperationException("Unauthorized");
        }

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(CollectionNotFoundException::new);

        if (!collection.getUser().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("You are not the owner");
        }

        collectionRepository.delete(collection);
    }

}
