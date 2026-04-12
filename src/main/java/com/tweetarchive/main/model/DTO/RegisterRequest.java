package com.tweetarchive.main.model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
  @NotBlank(message = "{username.required}")
  @Size(min = 3, message = "{username.size}")
  private String username;

  @NotBlank(message = "{password.required}")
  @Size(min = 6, message = "{password.size}")
  private String password;
  @Email
  private  String email;
}