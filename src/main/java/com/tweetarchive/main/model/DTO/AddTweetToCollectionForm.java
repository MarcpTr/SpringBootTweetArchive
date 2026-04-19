package com.tweetarchive.main.model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddTweetToCollectionForm {

        @NotBlank(message = "El link es obligatorio")
        @Size(max = 100, message = "Máximo 100 caracteres")
        @Pattern(regexp = "^(https?:\\/\\/(?:www\\.)?(?:x\\.com|twitter\\.com)\\/[A-Za-z0-9_]+\\/status\\/\\d+)$", message = "No es un enlace válido")
        String tweetLink;
}