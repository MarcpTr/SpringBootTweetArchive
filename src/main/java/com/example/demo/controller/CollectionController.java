package com.example.demo.controller;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.model.Collection;
import com.example.demo.repository.CollectionRepository;
import com.example.demo.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.module.ResolutionException;
import java.sql.Timestamp;
import java.util.List;

@Controller
public class CollectionController {
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private CollectionRepository collectionRepository;

    @GetMapping("/")
    public String listCollection(Model model){
        List<Collection> collections= collectionRepository.findAll();
        model.addAttribute("collections", collections);
        return "viewCollections";
    }
    @GetMapping("/create-collection")
    public String createCollectionForm(Model model){
        model.addAttribute("collection", new Collection());
        return "createCollection";
    }
    @PostMapping("/create-collection")
    public String createGroup(@RequestParam String collectionName){
        Collection collection= collectionService.createCollection(collectionName);
        return "redirect:/collection/" +collection.getId();
    }

     @GetMapping("/collection/{collectionId}")
    public String viewCollection(@PathVariable Long collectionId, Model model){
        Collection collection= collectionRepository.findById(collectionId).orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
         collection.setLastVisitedAt(new Timestamp(System.currentTimeMillis()));
         collectionRepository.save(collection);

         model.addAttribute("collection", collection);
         return "viewCollection";
     }

     @PostMapping("/collection/{collectionId}/add-tweet")
    public String addTweetToCollection(@PathVariable Long collectionId, @RequestParam String tweetLink){
        Collection collection = collectionRepository.findById(collectionId).orElseThrow(() -> new RuntimeException("Collection not found"));
         collectionService.addTweetToCollection(tweetLink, collection);
         return "redirect:/collection/" + collectionId;
     }

    @Scheduled(cron="0 0 0 * * ?")
    public void deleteOldCollections(){
        collectionService.checkAndDeleteOldCollections();
    }
}
