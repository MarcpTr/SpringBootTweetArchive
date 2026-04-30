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

/**
 * Configuración de seguridad de la aplicación.
 *
 * <p>
 * Define las reglas de acceso a endpoints, configuración de login/logout,
 * persistencia de sesión (remember-me) y mecanismos de autenticación.
 *
 * <p>
 * Se utiliza {@link SecurityFilterChain} para configurar la seguridad basada
 * en filtros HTTP en aplicaciones modernas de Spring.
 */
@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/register", "/login", "/register", "/",
                                                                "/error", "/user/**", "/colecction/{id}", "/js/**",
                                                                "/css/**", "/images/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/collection/create").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/collection/**").permitAll()
                                                .requestMatchers(HttpMethod.DELETE, "/api/collection/**")
                                                .authenticated()

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

        /**
         * Proporciona el {@link AuthenticationManager} utilizado para la autenticación.
         *
         * <p>
         * Delega en la configuración automática de Spring para obtener
         * el gestor de autenticación configurado.
         *
         * @param config configuración de autenticación de Spring
         * @return {@link AuthenticationManager}
         * @throws Exception en caso de error al obtenerlo
         */
        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        /**
         * Define el codificador de contraseñas.
         *
         * <p>
         * Se utiliza {@link BCryptPasswordEncoder} por ser un algoritmo seguro
         * basado en hashing con salt incorporado.
         *
         * @return {@link PasswordEncoder} basado en BCrypt
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
