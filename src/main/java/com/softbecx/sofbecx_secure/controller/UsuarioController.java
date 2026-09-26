package com.softbecx.sofbecx_secure.controller;

import com.softbecx.sofbecx_secure.model.Empresa;
import com.softbecx.sofbecx_secure.model.Rol;
import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.repository.EmpresaRepository;
import com.softbecx.sofbecx_secure.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final EmpresaRepository empresaRepository;

    public UsuarioController(
            UsuarioService usuarioService,
            EmpresaRepository empresaRepository) {

        this.usuarioService = usuarioService;
        this.empresaRepository = empresaRepository;
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {

        model.addAttribute(
                "usuario",
                new Usuario()
        );

        cargarEmpresas(model);

        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {

            cargarEmpresas(model);

            return "registro";
        }

        if (usuario.getEmpresa() == null) {

            model.addAttribute(
                    "empresaObligatoria",
                    "Debe seleccionar una empresa"
            );

            cargarEmpresas(model);

            return "registro";
        }

        if (usuarioService.existeCorreo(usuario.getCorreo())) {

            model.addAttribute(
                    "correoDuplicado",
                    "El correo electrónico ya está registrado"
            );

            cargarEmpresas(model);

            return "registro";
        }

        usuario.setRol(Rol.PROVEEDOR);

        usuarioService.guardarUsuario(usuario);

        return "redirect:/registro";
    }

    private void cargarEmpresas(Model model) {

        List<Empresa> empresas =
                empresaRepository.findAll();

        model.addAttribute(
                "empresas",
                empresas
        );
    }
}