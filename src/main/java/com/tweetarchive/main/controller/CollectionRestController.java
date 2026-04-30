package com.tweetarchive.main.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.DTO.ApiResponse;
import com.tweetarchive.main.model.DTO.LikeResponse;
import com.tweetarchive.main.model.DTO.VisibilityResponse;
import com.tweetarchive.main.service.CollectionLikeService;
import com.tweetarchive.main.service.CollectionService;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de colecciones.
 *
 * <p>
 * Expone endpoints para:
 * <ul>
 * <li>Cambiar visibilidad de colecciones</li>
 * <li>Eliminar colecciones</li>
 * <li>Eliminar tweets de colecciones</li>
 * <li>Dar y quitar "like" a colecciones</li>
 * </ul>
 *
 * <p>
 * Todos los endpoints devuelven respuestas en formato JSON.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/collection")
public class CollectionRestController {
    private final CollectionService collectionService;
    private final CollectionLikeService likeService;

    /**
     * Cambia la visibilidad de una colección (pública/privada).
     *
     * @param collectionId identificador de la colección
     * @return respuesta con el nuevo estado de visibilidad
     */
    @PutMapping("/{collectionId}/visibility")
    public ResponseEntity<ApiResponse<VisibilityResponse>> changeVisibility(@PathVariable long collectionId) {
        return ResponseEntity.ok(ApiResponse.ok(collectionService.changeVisibility(collectionId)));
    }

    /**
     * Elimina una colección.
     *
     * @param collectionId identificador de la colección
     * @param user         usuario autenticado
     * @return respuesta sin contenido (204) si la operación es exitosa
     */
    @DeleteMapping("/{collectionId}")
    public ResponseEntity<?> deleteCollection(@PathVariable long collectionId,
            @AuthenticationPrincipal CustomUserDetails user) {
        collectionService.deleteCollection(collectionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina un tweet de una colección.
     *
     * @param collectionId identificador de la colección
     * @param tweetId      identificador del tweet
     * @return respuesta sin contenido (204) si la operación es exitosa
     */
    @DeleteMapping("/{collectionId}/tweets/{tweetId}")
    public ResponseEntity<Map<String, Object>> removeTweetFromCollection(
            @PathVariable Long collectionId,
            @PathVariable Long tweetId) {
        collectionService.deleteTweet(collectionId, tweetId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Añade un "like" a una colección.
     *
     * @param collectionId identificador de la colección
     * @return respuesta con el estado actualizado del "like"
     */
    @PostMapping("/{collectionId}/like")
    public ResponseEntity<ApiResponse<LikeResponse>> like(
            @PathVariable Long collectionId) {
        return ResponseEntity.ok(ApiResponse.ok(likeService.like(collectionId)));
    }

    /**
     * Elimina el "like" de una colección.
     *
     * @param collectionId identificador de la colección
     * @return respuesta sin contenido (204) si la operación es exitosa
     */
    @DeleteMapping("/{collectionId}/like")
    public ResponseEntity<Void> unlike(
            @PathVariable Long collectionId) {
        likeService.unlike(collectionId);
        return ResponseEntity.noContent().build();
    }
}