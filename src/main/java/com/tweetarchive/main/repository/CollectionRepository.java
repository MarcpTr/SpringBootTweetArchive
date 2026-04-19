package com.tweetarchive.main.repository;

import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.DTO.CollectionPreviewDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

    List<Collection> findByLastVisitedAtBefore(LocalDateTime time);

   List<Collection>  findAllByUserId(Long id);

    List<Collection> findByIsPublic(boolean isPublic);

    List<Collection> findByIsPublicAndUserId(boolean isPublic, long userId);
    Optional<Collection> findByIdAndUserId(long colectionId, long userId);
    @Query("""
                SELECT new com.tweetarchive.main.model.DTO.CollectionPreviewDTO(
                    c.user.username,
                    c.user.id,
                    c.id,
                    c.name,
                    c.isPublic,
                    MAX(t.tweet),
                    COUNT(t.id)
                )
                FROM Collection c
                LEFT JOIN Tweet t ON t.collection.id = c.id
                WHERE c.id IN :ids
                GROUP BY c.user.username, c.user.id, c.id, c.name
            """)
    List<CollectionPreviewDTO> findByIdsWithPreviewTweet(@Param("ids") List<Long> ids);
}
