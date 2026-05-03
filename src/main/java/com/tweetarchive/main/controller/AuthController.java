package com.tweetarchive.main.controller;

import com.tweetarchive.main.model.DTO.RegisterRequest;
import com.tweetarchive.main.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador encargado de la autenticación y registro de usuarios.
 *
 * <p>
 * Gestiona:
 * <ul>
 * <li>Visualización del login</li>
 * <li>Registro de nuevos usuarios</li>
 * <li>Autenticación automática tras registro</li>
 * </ul>
 *
 * <p>
 * Tras un registro exitoso, el usuario es autenticado manualmente
 * y se establece en el contexto de seguridad sin necesidad de login adicional.
 */
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserDetailsService userDetailsService;

    /**
     * Muestra la página de login.
     *
     * <p>
     * Opcionalmente muestra mensajes de error o logout según los parámetros
     * recibidos.
     *
     * @param error  indica si hubo error en el login
     * @param logout indica si el usuario cerró sesión correctamente
     * @param model  modelo para enviar atributos a la vista
     * @return nombre de la vista "login"
     */
    @GetMapping("login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout, Model model) {
        if (error != null) {
            model.addAttribute("error", "login.error" + //
                    "");
        }

        if (logout != null) {
            model.addAttribute("message", "logout");
        }
        return "login";
    }

    /**
     * Muestra el formulario de registro.
     *
     * <p>
     * Inicializa un objeto vacío {@link RegisterRequest}
     * para el binding del formulario.
     *
     * @param model modelo para la vista
     * @return nombre de la vista "register"
     */
    @GetMapping("register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    /**
     * Procesa el registro de un nuevo usuario.
     *
     * <p>
     * Flujo:
     * <ol>
     * <li>Valida los datos del formulario</li>
     * <li>Si hay errores, vuelve a la vista de registro</li>
     * <li>Si es correcto, registra al usuario</li>
     * <li>Autentica automáticamente al usuario</li>
     * <li>Guarda el contexto de seguridad en sesión</li>
     * </ol>
     *
     * @param request       datos del formulario de registro
     * @param bindingResult resultado de validaciones
     * @param httpRequest   petición HTTP para gestionar la sesión
     * @return redirección a la página principal o vista de registro si hay errores
     */
    @PostMapping("register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult bindingResult, HttpServletRequest httpRequest) {

        Map<String, String> errors = userService.validateRegister(registerRequest);

        errors.forEach((field, messageCode) -> bindingResult.rejectValue(field, messageCode));

        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.registerUser(registerRequest);

        UserDetails userDetails = userDetailsService.loadUserByUsername(registerRequest.getUsername());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        httpRequest.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return "redirect:/";
    }
}
