package com.tweetarchive.main.service;

import com.tweetarchive.main.model.CustomUserDetails;
import com.tweetarchive.main.model.User;
import com.tweetarchive.main.model.DTO.RegisterRequest;
import com.tweetarchive.main.model.enums.Role;
import com.tweetarchive.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio encargado de la gestión de usuarios.
 *
 * <p>
 * Responsabilidades principales:
 * <ul>
 * <li>Validaciones de negocio en el registro</li>
 * <li>Persistencia de usuarios</li>
 * <li>Integración con el sistema de autenticación de Spring</li>
 * </ul>
 *
 * <p>
 * Implementa {@link UserDetailsService} para permitir a Spring Security
 * cargar usuarios durante el proceso de autenticación.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Realiza validaciones de negocio para el registro de usuarios.
     *
     * <p>
     * Complementa las validaciones declarativas del DTO ({@link RegisterRequest}),
     * como anotaciones {@code @NotNull}, {@code @Email}, etc.
     *
     * <p>
     * Valida:
     * <ul>
     * <li>Unicidad del username</li>
     * <li>Unicidad del email</li>
     * </ul>
     *
     * @param request datos del formulario de registro
     * @return mapa de errores donde:
     *         <ul>
     *         <li>clave = nombre del campo</li>
     *         <li>valor = código de mensaje (i18n)</li>
     *         </ul>
     */
    public Map<String, String> validateRegister(RegisterRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (userRepository.existsByUsername(request.getUsername())) {
            errors.put("username", "username.alreadyexist");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            errors.put("email", "email.alreadyexist");
        }

        return errors;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * <p>
     * Realiza:
     * <ul>
     * <li>Mapeo de {@link RegisterRequest} a entidad {@link User}</li>
     * <li>Codificación segura de la contraseña</li>
     * <li>Asignación de rol por defecto</li>
     * <li>Persistencia en base de datos</li>
     * </ul>
     *
     * <p>
     * <b>Nota:</b> Este método solo registra al usuario.
     * La autenticación posterior se realiza en el controlador.
     *
     * @param request datos del usuario a registrar
     */
    @Transactional
    public void registerUser(RegisterRequest request) {

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
    }

    /**
     * Carga un usuario por su username para el proceso de autenticación.
     *
     * <p>
     * Este método es utilizado automáticamente por Spring Security
     * durante el login.
     *
     * <p>
     * Convierte la entidad {@link User} en un objeto {@link UserDetails}
     * mediante {@link CustomUserDetails}.
     *
     * @param username nombre de usuario
     * @return detalles del usuario para autenticación
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username.notfound"));

        return new CustomUserDetails(user);
    }
}