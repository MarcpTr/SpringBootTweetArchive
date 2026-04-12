package com.tweetarchive.main.model.DTO;

import java.util.ArrayList;
import java.util.List;

import com.tweetarchive.main.model.Tweet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionDTO {
    private Long id;
    private String name;
    private String username;
    private Long userId;
    private List<Tweet> tweets;
}