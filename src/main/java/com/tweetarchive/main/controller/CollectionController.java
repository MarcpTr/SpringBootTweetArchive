package com.tweetarchive.main.controller;

import com.tweetarchive.main.exceptions.InvalidCredentialsException;
import com.tweetarchive.main.exceptions.TweetAlreadyExistsException;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.DTO.AddTweetToCollectionForm;
import com.tweetarchive.main.model.DTO.CollectionDTO;
import com.tweetarchive.main.model.DTO.CollectionPreviewDTO;
import com.tweetarchive.main.model.DTO.CreateCollectionForm;
import com.tweetarchive.main.service.CollectionService;
import com.tweetarchive.main.service.TweetService;
import com.tweetarchive.main.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;
    private final UserService userService;
    private final TweetService tweetService;

    @GetMapping("/")
    public String listPublicCollections(Model model) {
        List<CollectionPreviewDTO> collections = collectionService.findPublicCollections(true);
        model.addAttribute("collections", collections);
        return "index";
    }

    @GetMapping("/my-collections")
    public String listMyCollections(Model model) {
        List<CollectionPreviewDTO> collections = collectionService.findMyCollections();
        model.addAttribute("collections", collections);
        return "myCollections";
    }

    @GetMapping("/user/{username}")
    public String viewUserCollections(@PathVariable String username, Model model) {
        List<CollectionPreviewDTO> collections = collectionService.findUserCollections(username);
        model.addAttribute("collections", collections);
        return "userCollections";
    }

    @GetMapping("/collection/{collectionId}")
    public String viewCollection(@PathVariable Long collectionId, Model model) {
        boolean isCreator = false;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        long currentId;
        CollectionDTO collectionDTO = collectionService.viewCollection(collectionId);

        if (principal instanceof CustomUserDetails) {
            currentId = ((CustomUserDetails) principal).getId();
            isCreator = collectionDTO.getUserId() == currentId ? true : false;
        }
        model.addAttribute("collection", collectionDTO);
        model.addAttribute("isCreator", isCreator);
        model.addAttribute("form", new AddTweetToCollectionForm());
        return "viewCollection";
    }

    @GetMapping("/collection/create")
    public String showForm(Model model) {
        model.addAttribute("form", new CreateCollectionForm());
        return "createcollection";
    }

    @PostMapping("/collection/create")
    public String createCollection(@Valid @ModelAttribute("form") CreateCollectionForm form,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "createcollection";
        }
        try {
            Long id = collectionService.createCollection(
                    form.getCollectionName(),
                    form.isPublic());
            return "redirect:/collection/" + id;
        } catch (InvalidCredentialsException e) {
            bindingResult.reject("globalError", "Usuario no autenticado");
            return "createcollection";
        }
    }

    @PostMapping("/collection/{collectionId}/add-tweet")
public String addTweetToCollection(
        @PathVariable Long collectionId,
        @Valid @ModelAttribute("form") AddTweetToCollectionForm form,
        BindingResult bindingResult,
        Model model) {

    if (bindingResult.hasErrors()) {
        CollectionDTO collection = collectionService.viewCollection(collectionId);
        model.addAttribute("collection", collection);
        return "viewCollection";
    }

    try {
        tweetService.addTweetToCollection(collectionId, form.getTweetLink());

    } catch (TweetAlreadyExistsException e) {

        bindingResult.rejectValue(
            "tweetLink",
            "error.tweetLink",
            "Este tweet ya está en la colección"
        );

        CollectionDTO collection = collectionService.viewCollection(collectionId);
        model.addAttribute("collection", collection);

        return "viewCollection";
    }

    return "redirect:/collection/" + collectionId;
}
  
}


  /*
     * @GetMapping("/search")
     * public String search(Model model, @RequestParam Optional<String> query) {
     * String searchQuery = query.orElse("");
     * if (!searchQuery.equals("")) {
     * List<Collection> collections =
     * collectionService.searchByNameFuzzy(searchQuery)
     * .orElseThrow(() -> new RuntimeException("Collection not found"));
     * model.addAttribute("collections", collections);
     * model.addAttribute("query", searchQuery);
     * }
     * return "search";
     * }
     * 
     * 
     * 
     * @DeleteMapping("/collection/{collectionId}/remove-tweet")
     * public ResponseEntity<Map<String, Object>>
     * removeTweetFromCollection(@PathVariable Long collectionId,
     * 
     * @RequestParam String tweetId) {
     * Map<String, Object> response = new HashMap<>();
     * Authentication authentication =
     * SecurityContextHolder.getContext().getAuthentication();
     * String username = authentication.getName();
     * User user = userService.findByUsername(username)
     * .orElseThrow(() -> new RuntimeException("User not found"));
     * Tweet tweet = tweetService.findById(Long.parseLong(tweetId))
     * .orElseThrow(() -> new RuntimeException("Tweet not found"));
     * Collection collection =
     * collectionService.findById(tweet.getCollection().getId())
     * .orElseThrow(() -> new RuntimeException("Collection not found"));
     * if (user.getId().equals(collection.getUser().getId())) {
     * tweetService.delete(tweet);
     * response.put("status", "success");
     * response.put("message", "Tweet removed from collection");
     * return ResponseEntity.ok(response);
     * }
     * 
     * response.put("status", "error");
     * response.put("message", "Something went wrong: ");
     * 
     * return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
     * }
     * 
     * @DeleteMapping("/collection/{collectionId}/remove-collection")
     * public ResponseEntity<Map<String, Object>> removeCollection(@PathVariable
     * Long collectionId) {
     * Map<String, Object> response = new HashMap<>();
     * Authentication authentication =
     * SecurityContextHolder.getContext().getAuthentication();
     * String username = authentication.getName();
     * User user = userService.findByUsername(username)
     * .orElseThrow(() -> new RuntimeException("User not found"));
     * Collection collection =
     * collectionService.findById(collectionId).orElseThrow();
     * if (user.getId().equals(collection.getUser().getId())) {
     * collectionService.delete(collection);
     * response.put("status", "success");
     * response.put("message", "Collection removed");
     * return ResponseEntity.ok(response);
     * }
     * 
     * response.put("status", "error");
     * response.put("message", "Something went wrong: ");
     * 
     * return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
     * }
     */