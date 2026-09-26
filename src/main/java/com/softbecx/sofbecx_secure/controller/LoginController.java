package com.softbecx.sofbecx_secure.controller;

import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam("correo") String correo,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        Optional<Usuario> usuario =
                loginService.autenticar(correo, password);

        if (usuario.isEmpty()) {
            model.addAttribute(
                    "error",
                    "Correo o contraseña incorrectos"
            );

            return "login";
        }

        session.setAttribute(
                "usuarioId",
                usuario.get().getId()
        );

        session.setAttribute(
                "rol",
                usuario.get().getRol()
        );

        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}