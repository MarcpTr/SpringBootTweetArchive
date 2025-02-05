package com.tweetarchive.main.model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "last_visited_at")
    private Timestamp lastVisitedAt;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.REMOVE)
    private List<Tweet> tweets;
    @ManyToOne
    @JoinColumn(name = "user_id",  nullable = false)
    private User user;
    @Column(name = "public")
    private boolean isPublic;

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastVisitedAt() {
        return lastVisitedAt;
    }

    public void setLastVisitedAt(Timestamp lastVisitedAt) {
        this.lastVisitedAt = lastVisitedAt;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }
}
