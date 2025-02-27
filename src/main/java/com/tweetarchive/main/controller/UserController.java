package com.tweetarchive.main.controller;

import com.tweetarchive.main.model.Collection;
import com.tweetarchive.main.model.User;
import com.tweetarchive.main.service.CollectionService;
import com.tweetarchive.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller

public class UserController {
 @Autowired
    UserService userService;
    @Autowired
    CollectionService collectionService;
    @GetMapping("/user/{userId}")
    public String viewUserCollections(@PathVariable Long userId, Model model){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        User user = userService.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Collection> collections= collectionService.findByIsPublicandUserId(true, user.getId()).orElseThrow();

        model.addAttribute("collections", collections);
        model.addAttribute("username", user.getUsername());
        return "userCollections";
    }
}
