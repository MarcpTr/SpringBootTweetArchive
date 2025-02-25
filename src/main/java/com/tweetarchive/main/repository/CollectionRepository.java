package com.tweetarchive.main.repository;

import com.tweetarchive.main.model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByLastVisitedAtBefore(Timestamp timestamp);
    @Query(value = "SELECT * FROM collection WHERE MATCH(name) AGAINST(:name IN NATURAL LANGUAGE MODE WITH QUERY EXPANSION)  AND public = 1", nativeQuery = true)
    Optional<List<Collection>>  searchByNameFuzzy(@Param("name") String name);
    Optional<List<Collection>> findByUserId(Long id);
    Optional<List<Collection>> findByIsPublic(boolean isPublic);
    Optional<List<Collection>> findByIsPublicAndUserId(boolean isPublic, long userId);
}
