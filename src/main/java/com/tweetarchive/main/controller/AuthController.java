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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserDetailsService userDetailsService;

    /**
     * Maneja la visualización de la página de inicio de sesión.
     *
     * <p>
     * Este método procesa posibles parámetros de estado provenientes del flujo
     * de autenticación gestionado por Spring Security:
     * </p>
     * <ul>
     * <li>Si existe el parámetro {@code error}, indica que la autenticación ha
     * fallado
     * y se añade un mensaje de error al modelo.</li>
     * <li>Si existe el parámetro {@code logout}, indica que el usuario ha cerrado
     * sesión
     * correctamente y se añade un mensaje informativo al modelo.</li>
     * </ul>
     *
     * @param error  parámetro opcional que indica un fallo en el inicio de sesión
     * @param logout parámetro opcional que indica un cierre de sesión exitoso
     * @param model  modelo utilizado para pasar atributos a la vista
     * @return nombre de la vista de login ({@code "login"})
     */
    @GetMapping("login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout, Model model) {
                if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }

        if (logout != null) {
            model.addAttribute("message", "Sesión cerrada correctamente");
        }
        return "login";
    }

    @GetMapping("register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    /**
     * Maneja la solicitud de registro de un nuevo usuario.
     *
     * <p>
     * Flujo del método:
     * </p>
     * <ol>
     * <li>Valida los datos recibidos en el DTO {@code RegisterRequest}.</li>
     * <li>Si hay errores de validación, retorna la vista de registro.</li>
     * <li>Delega la creación del usuario a la capa de servicio.</li>
     * <li>Si el usuario ya existe, añade errores al {@code BindingResult}
     * asociados al campo correspondiente (username o email).</li>
     * <li>Si el registro es exitoso, autentica automáticamente al usuario.</li>
     * <li>Almacena el contexto de seguridad en la sesión HTTP.</li>
     * <li>Aplica el patrón PRG (Post/Redirect/Get) redirigiendo al inicio.</li>
     * </ol>
     *
     * @param request       objeto con los datos del formulario de registro,
     *                      validado con {@code @Valid}
     * @param bindingResult contiene los resultados de la validación y posibles
     *                      errores
     * @param httpRequest   request HTTP actual, usado para gestionar la sesión
     * @return nombre de la vista:
     *         <ul>
     *         <li>{@code "register"} si hay errores de validación o de negocio</li>
     *         <li>{@code "redirect:/"} si el registro es exitoso</li>
     *         </ul>
     *
     * @throws UserAlreadyExistsException si el username o email ya están
     *                                    registrados
     */
    @PostMapping("register")
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
