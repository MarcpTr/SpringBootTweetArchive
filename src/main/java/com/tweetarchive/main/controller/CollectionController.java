package com.tweetarchive.main.controller;

import com.tweetarchive.main.exceptions.InvalidCredentialsException;
import com.tweetarchive.main.exceptions.TweetAlreadyExistsException;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.DTO.AddTweetToCollectionForm;
import com.tweetarchive.main.model.DTO.CollectionDTO;
import com.tweetarchive.main.model.DTO.CreateCollectionForm;
import com.tweetarchive.main.service.CollectionService;
import com.tweetarchive.main.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;
    private final TweetService tweetService;

    @GetMapping("/")
    public String listPublicCollections(Model model) {
        model.addAttribute("collections", collectionService.findPublicCollections(true));
        return "index";
    }

    @GetMapping("/dashboard")
    public String listMyCollections(Model model) {
        model.addAttribute("collections", collectionService.findMyCollections());
        return "dashboard";
    }

    @GetMapping("/user/{username}")
    public String viewUserCollections(@PathVariable String username, Model model) {
        model.addAttribute("collections", collectionService.findUserCollections(username));
        return "collections";
    }

    @GetMapping("/collection/{collectionId}")
    public String viewCollection( 
            @PathVariable Long collectionId,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {

        CollectionDTO collection = collectionService.viewCollection(collectionId);
        boolean isCreator = user != null && collection.getUserId().equals(user.getId());

        model.addAttribute("collection", collection);
        model.addAttribute("isCreator", isCreator);
        model.addAttribute("form", new AddTweetToCollectionForm());

        return "viewCollection";
    }

    @GetMapping("/collection/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createCollectionForm", new CreateCollectionForm());
        return "createCollection";
    }

    @PostMapping("/collection/create")
    public String createCollection(
            @Valid @ModelAttribute("createCollectionForm") CreateCollectionForm form,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "createCollection";
        }

        try {
            Long id = collectionService.createCollection(
                    form.getCollectionName(),
                    form.isPublicCollection());
            return "redirect:/collection/" + id;

        } catch (InvalidCredentialsException e) {
            bindingResult.reject("globalError", "Usuario no autenticado");
            return "createCollection";
        }
    }

    @PostMapping("/collection/{collectionId}/add-tweet")
    public String addTweetToCollection(
            @PathVariable Long collectionId,
            @Valid @ModelAttribute("form") AddTweetToCollectionForm form,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {
        CollectionDTO collection = collectionService.viewCollection(collectionId);
        boolean isCreator = user != null && collection.getUserId() == user.getId();

        if (bindingResult.hasErrors()) {
            return loadCollectionView(model, collection, isCreator);
        }

        try {
            tweetService.addTweetToCollection(collectionId, form.getTweetLink());

        } catch (TweetAlreadyExistsException e) {
            bindingResult.rejectValue(
                    "tweetLink",
                    "error.tweetLink",
                    "Este tweet ya está en la colección");

            return loadCollectionView(model, collection, isCreator);
        }

        return "redirect:/collection/" + collectionId;
    }

    // Método reutilizable para evitar duplicación
    private String loadCollectionView(Model model, CollectionDTO collection, boolean isCreator) {
        model.addAttribute("collection", collection);
        model.addAttribute("isCreator", isCreator);
        return "viewCollection";
    }
}
   