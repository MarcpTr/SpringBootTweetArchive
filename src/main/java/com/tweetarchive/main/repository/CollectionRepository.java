package com.tweetarchive.main.repository;

import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.DTO.CollectionPreviewDTO;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

    @Query("SELECT c.id FROM Collection c WHERE c.isPublic = true")
    List<Long> findPublicIds();

    @Query("SELECT c.id FROM Collection c WHERE c.user.id = :id")
    List<Long> findAllIdsByUserId(@Param("id") Long id);

    @Query("SELECT c.id FROM Collection c WHERE c.isPublic = true and c.user.id= :id")
    List<Long> findByIsPublicAndUserId(@Param("id") Long id);

    Optional<Collection> findByIdAndUserId(long colectionId, long userId);

    @Query("""
                SELECT new com.tweetarchive.main.model.DTO.CollectionPreviewDTO(
                    c.user.username,
                    c.user.id,
                    c.id,
                    c.name,
                    c.isPublic,
                    MAX(t.tweet),
                    COUNT(t.id),

                    (SELECT COUNT(cl) FROM CollectionLike cl WHERE cl.collection.id = c.id),

                    CASE
                        WHEN EXISTS (
                            SELECT cl2.id
                            FROM CollectionLike cl2
                            WHERE cl2.collection.id = c.id
                            AND cl2.user.id = :userId
                        )
                        THEN true
                        ELSE false
                    END
                )
                FROM Collection c
                LEFT JOIN Tweet t ON t.collection.id = c.id
                WHERE c.id IN :ids
                GROUP BY c.user.username, c.user.id, c.id, c.name, c.isPublic
            """)
    List<CollectionPreviewDTO> findByIdsWithPreviewTweet(
            @Param("ids") List<Long> ids,
            @Param("userId") Long userId);

    @Query("""
                SELECT new com.tweetarchive.main.model.DTO.CollectionPreviewDTO(
                    c.user.username,
                    c.user.id,
                    c.id,
                    c.name,
                    c.isPublic,
                    MAX(t.tweet),
                    COUNT(t.id),
                    (SELECT COUNT(cl) FROM CollectionLike cl WHERE cl.collection.id = c.id),
                    false
                )
                FROM Collection c
                LEFT JOIN Tweet t ON t.collection.id = c.id
                WHERE c.id IN :ids
                GROUP BY c.user.username, c.user.id, c.id, c.name, c.isPublic
            """)
    List<CollectionPreviewDTO> findByIdsWithPreviewTweetNoUser(@Param("ids") List<Long> ids);

    @Query("""
                SELECT new com.tweetarchive.main.model.DTO.CollectionPreviewDTO(
                    c.user.username,
                    c.user.id,
                    c.id,
                    c.name,
                    c.isPublic,
                    MAX(t.tweet),
                    COUNT(t.id),

                    (SELECT COUNT(cl) FROM CollectionLike cl WHERE cl.collection.id = c.id),

                    CASE
                        WHEN EXISTS (
                            SELECT cl2.id
                            FROM CollectionLike cl2
                            WHERE cl2.collection.id = c.id
                            AND cl2.user.id = :userId
                        )
                        THEN true
                        ELSE false
                    END
                )
                FROM Collection c
                LEFT JOIN Tweet t ON t.collection.id = c.id
                WHERE c.isPublic = true
                GROUP BY c.user.username, c.user.id, c.id, c.name, c.isPublic
                HAVING COUNT(t.id) > 0
                ORDER BY
                    (SELECT COUNT(cl) FROM CollectionLike cl WHERE cl.collection.id = c.id) DESC
            """)
    List<CollectionPreviewDTO> findTopCollections(
            @Param("userId") Long userId,
            Pageable pageable);
}
