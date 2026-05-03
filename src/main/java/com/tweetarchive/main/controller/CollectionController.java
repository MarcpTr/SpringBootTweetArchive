package com.tweetarchive.main.controller;

import com.tweetarchive.main.exceptions.AuthenticationException;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.DTO.AddTweetToCollectionForm;
import com.tweetarchive.main.model.DTO.CollectionDTO;
import com.tweetarchive.main.model.DTO.CreateCollectionForm;
import com.tweetarchive.main.model.enums.AddTweetResult;
import com.tweetarchive.main.service.CollectionService;
import com.tweetarchive.main.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador encargado de la gestión de colecciones y navegación principal.
 *
 * <p>
 * Gestiona:
 * <ul>
 * <li>Listado de colecciones públicas destacadas</li>
 * <li>Panel del usuario (dashboard)</li>
 * <li>Visualización de colecciones por usuario</li>
 * <li>Detalle de una colección y sus tweets</li>
 * <li>Creación de colecciones</li>
 * <li>Añadir tweets a colecciones</li>
 * </ul>
 */
@Controller
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;
    private final TweetService tweetService;

    /**
     * Página principal.
     *
     * <p>
     * Muestra una lista de las mejores colecciones públicas.
     *
     * @param model modelo para la vista
     * @return vista "index"
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("collections", collectionService.findBestCollections());
        return "index";
    }

    /**
     * Panel del usuario autenticado (dashboard).
     *
     * <p>
     * Muestra las colecciones del usuario y permite gestionarlas
     * (visibilidad, eliminación, etc.).
     *
     * @param model modelo para la vista
     * @return vista "dashboard"
     */
    @GetMapping("/dashboard")
    public String DashboardView(Model model) {
        model.addAttribute("collections", collectionService.findMyCollections());
        return "dashboard";
    }

    /**
     * Muestra las colecciones públicas de un usuario.
     *
     * @param username nombre del usuario
     * @param model    modelo para la vista
     * @return vista "user-collections"
     */
    @GetMapping("/user/{username}")
    public String userCollections(@PathVariable String username, Model model) {
        model.addAttribute("collections", collectionService.findUserCollections(username));
        return "user-collections";
    }

    /**
     * Muestra el detalle de una colección.
     *
     * <p>
     * Incluye:
     * <ul>
     * <li>Lista de tweets de la colección</li>
     * <li>Formulario para añadir tweets (solo si es el creador)</li>
     * <li>Opciones de gestión si el usuario es propietario</li>
     * </ul>
     *
     * @param collectionId identificador de la colección
     * @param user         usuario autenticado (puede ser null)
     * @param model        modelo para la vista
     * @return vista "collection"
     */
    @GetMapping("/collection/{collectionId}")
    public String collection(
            @PathVariable Long collectionId,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {

        CollectionDTO collection = collectionService.viewCollection(collectionId);
        boolean isCreator = user != null && collection.getUserId().equals(user.getId());
        // Mantiene el formulario si viene de un redirect con errores
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new AddTweetToCollectionForm());
        }

        model.addAttribute("collection", collection);
        model.addAttribute("isCreator", isCreator);

        return "collection";
    }

    /**
     * Muestra el formulario de creación de colecciones.
     *
     * @param model modelo para la vista
     * @return vista "createCollection"
     */
    @GetMapping("/collection/create")
    public String CreateCollectionView(Model model) {
        model.addAttribute("createCollectionForm", new CreateCollectionForm());
        return "createCollection";
    }

    /**
     * Procesa la creación de una nueva colección.
     *
     * @param form          datos del formulario
     * @param bindingResult resultado de validaciones
     * @return redirección a la colección creada o vuelta al formulario si hay
     *         errores
     */
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

        } catch (AuthenticationException e) {
            bindingResult.reject("globalError", "Usuario no autenticado");
            return "createCollection";
        }
    }

    /**
     * Añade un tweet a una colección.
     *
     * <p>
     * Gestiona:
     * <ul>
     * <li>Validación del formulario</li>
     * <li>Errores de negocio (duplicado, no encontrado)</li>
     * <li>Mensajes flash para redirección</li>
     * </ul>
     *
     * @param collectionId       identificador de la colección
     * @param form               datos del formulario (link del tweet)
     * @param bindingResult      resultado de validación
     * @param user               usuario autenticado
     * @param model              modelo
     * @param redirectAttributes atributos flash para redirección
     * @return redirección a la vista de la colección
     */
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
        } else if (result == AddTweetResult.NOT_FOUND) {

            bindingResult.rejectValue("tweetLink", "tweet.notfound");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            redirectAttributes.addFlashAttribute("form", form);

            return "redirect:/collection/" + collectionId;
        }

        redirectAttributes.addFlashAttribute("successMessage", "Tweet añadido correctamente");
        return "redirect:/collection/" + collectionId;
    }
}
