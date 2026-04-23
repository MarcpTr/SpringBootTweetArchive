package com.tweetarchive.main.controller;

import com.tweetarchive.main.exceptions.InvalidCredentialsException;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.DTO.AddTweetToCollectionForm;
import com.tweetarchive.main.model.DTO.CollectionDTO;
import com.tweetarchive.main.model.DTO.CreateCollectionForm;
import com.tweetarchive.main.model.enums.AddTweetResult;
import com.tweetarchive.main.service.CollectionService;
import com.tweetarchive.main.service.TweetService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;
    private final TweetService tweetService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("collections", collectionService.findBestCollections());
        return "index";
    }

    @GetMapping("/dashboard")
    public String DashboardView(Model model) {
        model.addAttribute("collections", collectionService.findMyCollections());
        return "dashboard";
    }

    @GetMapping("/user/{username}")
    public String userCollections(@PathVariable String username, Model model) {
        model.addAttribute("collections", collectionService.findUserCollections(username));
        return "user-collections";
    }

    @GetMapping("/collection/{collectionId}")
    public String collection(
            @PathVariable Long collectionId,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {

        CollectionDTO collection = collectionService.viewCollection(collectionId);
        boolean isCreator = user != null && collection.getUserId().equals(user.getId());

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new AddTweetToCollectionForm());
        }

        model.addAttribute("collection", collection);
        model.addAttribute("isCreator", isCreator);

        return "collection";
    }

    @GetMapping("/collection/create")
    public String CreateCollectionView(Model model) {
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
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/collection/" + collectionId;
        }

        AddTweetResult result = tweetService.addTweetToCollection(collectionId, form.getTweetLink());

        if (result == AddTweetResult.ALREADY_EXISTS) {

            bindingResult.rejectValue("tweetLink", "tweet.duplicate");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            redirectAttributes.addFlashAttribute("form", form);

            return "redirect:/collection/" + collectionId;
        }

        redirectAttributes.addFlashAttribute("successMessage", "Tweet añadido correctamente");
        return "redirect:/collection/" + collectionId;
    }
}
