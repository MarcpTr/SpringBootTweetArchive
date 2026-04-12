package com.tweetarchive.main.controller;

import com.tweetarchive.main.exceptions.UserAlreadyExistsException;
import com.tweetarchive.main.model.DTO.RegisterRequest;
import com.tweetarchive.main.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserDetailsService userDetailsService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult, HttpServletRequest httpRequest) {

        // 1. Validación de formato (DTO)
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // 2. Delegación a lógica de negocio
        try {
            userService.registerAndLogin(request);
        } catch (UserAlreadyExistsException ex) {

            // Mapear error al campo correcto
            if (ex.getField().equals("username")) {
                bindingResult.rejectValue("username", "error.user", ex.getMessage());
            } else if (ex.getField().equals("email")) {
                bindingResult.rejectValue("email", "error.user", ex.getMessage());
            }

            return "register";
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        httpRequest.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        // PRG Pattern
        return "redirect:/";
    }
}
