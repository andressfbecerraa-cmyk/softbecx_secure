package com.softbecx.sofbecx_secure.controller;

import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {

        model.addAttribute("usuario", new Usuario());

        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "registro";
        }

        if (usuarioService.existeCorreo(usuario.getCorreo())) {
            model.addAttribute("correoDuplicado",
                    "El correo electrónico ya está registrado");

            return "registro";
        }

        usuarioService.guardarUsuario(usuario);

        return "redirect:/registro";
    }
}