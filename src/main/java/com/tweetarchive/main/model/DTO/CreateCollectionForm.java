package com.tweetarchive.main.model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectionForm {
        
        @NotBlank(message = "El nombre es obligatorio") 
        @Size(max = 200, message = "Máximo 20 caracteres") 
        String collectionName;
        boolean publicCollection;
}