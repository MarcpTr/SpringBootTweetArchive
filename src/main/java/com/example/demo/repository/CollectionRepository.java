package com.example.demo.repository;

import com.example.demo.model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByLastVisitedAtBefore(Timestamp timestamp);
}
