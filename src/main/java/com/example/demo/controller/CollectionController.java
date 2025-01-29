package com.example.demo.controller;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.model.Collection;
import com.example.demo.model.Tweet;
import com.example.demo.model.User;
import com.example.demo.repository.CollectionRepository;
import com.example.demo.repository.TweetRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CollectionService;
import com.example.demo.service.TweetService;
import com.example.demo.util.TweetLinkValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CollectionController {
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private CollectionRepository collectionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TweetRepository tweetRepository;
    @Autowired
    private TweetService tweetService;

    @GetMapping("/")
    public String listCollection(Model model){
        List<Collection> collections= collectionRepository.findByIsPublic(true).orElseThrow();
        model.addAttribute("collections", collections);
        return "viewCollections";
    }
    @GetMapping("/my-collections")
    public String listMyCollection(Model model){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Collection> collections= collectionRepository.findByUserId(user.getId()).orElseThrow();

        model.addAttribute("collections", collections);
        return "myCollections";
    }
    @GetMapping("/create-collection")
    public String createCollectionForm(Model model){
        model.addAttribute("collection", new Collection());
        return "createCollection";
    }
    @PostMapping("/create-collection")
    public String createCollection(@RequestParam String collectionName){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Collection collection= collectionService.createCollection(collectionName,user);
        return "redirect:/collection/" +collection.getId();
    }

     @GetMapping("/collection/{collectionId}")
    public String viewCollection(@PathVariable Long collectionId, Model model){
         boolean isCreator=false;
         Collection collection= collectionRepository.findById(collectionId).orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
         collection.setLastVisitedAt(new Timestamp(System.currentTimeMillis()));
         collectionRepository.save(collection);
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         if(authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))){
             String username = authentication.getName();
             User user = userRepository.findByUsername(username)
                     .orElseThrow(() -> new RuntimeException("User not found"));
             isCreator = collection.getUser().getId().equals(user.getId());
         }
         model.addAttribute("isCreator", isCreator);
         model.addAttribute("collection", collection);

         return "viewCollection";
     }

     @PostMapping("/collection/{collectionId}/add-tweet")
    public ResponseEntity<Map<String, Object>> addTweetToCollection(@PathVariable Long collectionId, @RequestParam String tweetLink){
         Map<String, Object> response = new HashMap<>();
        if(TweetLinkValidator.isTweetUrl(tweetLink)){
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String username = authentication.getName();
         User user = userRepository.findByUsername(username)
                 .orElseThrow(() -> new RuntimeException("User not found"));
        Collection collection = collectionRepository.findById(collectionId).orElseThrow(() -> new RuntimeException("Collection not found"));
        if(user.getId().equals(collection.getUser().getId())){
            tweetService.addTweet(tweetLink, collection);
            response.put("status", "success");
            response.put("message", "Tweet added to collection");
            return ResponseEntity.ok(response);
         }
        }
         response.put("status", "error");
         response.put("message", "Something went wrong: ");

         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
     }
    @DeleteMapping("/collection/{collectionId}/remove-tweet")
    public ResponseEntity<Map<String, Object>> removeTweetFromCollection(@PathVariable Long collectionId, @RequestParam String tweetId){
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Tweet tweet=tweetRepository.findById(Long.parseLong(tweetId)).orElseThrow();
        Collection collection= collectionRepository.findById(tweet.getCollection().getId()).orElseThrow();
        if(user.getId().equals(collection.getUser().getId())){
            tweetRepository.delete(tweet);
            response.put("status", "success");
            response.put("message", "Tweet removed from collection");
            return ResponseEntity.ok(response);
        }

         response.put("status", "error");
         response.put("message", "Something went wrong: ");

         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @DeleteMapping("/collection/{collectionId}/remove-collection")
    public ResponseEntity<Map<String, Object>> removeCollection(@PathVariable Long collectionId){
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Collection collection= collectionRepository.findById(collectionId).orElseThrow();
        if(user.getId().equals(collection.getUser().getId())){
            collectionRepository.delete(collection);
            response.put("status", "success");
            response.put("message", "Collection removed");
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Something went wrong: ");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @Scheduled(cron="0 0 0 * * ?")
    public void deleteOldCollections(){
        collectionService.checkAndDeleteOldCollections();
    }
}
