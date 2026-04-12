package com.tweetarchive.main.service;

import com.tweetarchive.main.exceptions.UserAlreadyExistsException;
import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.User;
import com.tweetarchive.main.model.DTO.RegisterRequest;
import com.tweetarchive.main.model.enums.Role;
import com.tweetarchive.main.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

 

    @Transactional
    public void registerAndLogin(RegisterRequest request) {

        // 1. Validaciones de negocio
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("username", "El usuario ya existe");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("email", "El email ya está en uso");
        }

        // 2. Crear entidad
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        // 3. Persistir
        userRepository.save(user);
   
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByUserId(long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(user);
    }
}