package com.tweetarchive.main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(columnNames = {"tweet", "collection_id"})
)
public class Tweet {
    @Id
    @GeneratedValue (strategy =GenerationType.IDENTITY)
    private Long id;

    private String tweet;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;

}
