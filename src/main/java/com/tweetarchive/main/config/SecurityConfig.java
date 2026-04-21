package com.tweetarchive.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/register", "/login", "/register", "/", 
                                                                "/error", "/user/**", "/colecction/{id}", "/js/**", "/css/**", "/images/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/collection/create").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/collection/**").permitAll()
                                                .requestMatchers(HttpMethod.DELETE, "/api/collection/**").authenticated()

                                                .requestMatchers(HttpMethod.POST, "/collection/**").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/collection/**").authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/collection/**").authenticated()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/", true)
                                                .failureUrl("/login?error")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/?logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID"))
                                .rememberMe(remember -> remember
                                                .key("super-secret-key")
                                                .tokenValiditySeconds(604800).alwaysRemember(false));

                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
