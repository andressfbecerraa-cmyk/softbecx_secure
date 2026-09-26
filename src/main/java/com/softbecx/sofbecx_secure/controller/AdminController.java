package com.softbecx.sofbecx_secure.controller;

import com.softbecx.sofbecx_secure.model.Rol;
import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    private final UsuarioRepository usuarioRepository;

    public AdminController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/admin/usuarios")
    public String mostrarUsuarios(
            HttpSession session,
            Model model) {

        Object usuarioId = session.getAttribute("usuarioId");
        Object rol = session.getAttribute("rol");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        if (!"ADMIN".equals(String.valueOf(rol))) {
            return "acceso-denegado";
        }

        List<Usuario> usuarios =
                usuarioRepository.findAll();

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("usuarioSesionId", usuarioId);

        return "admin-usuarios";
    }

    @PostMapping("/admin/usuarios/rol")
    public String cambiarRol(
            HttpSession session,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("rol") Rol nuevoRol,
            Model model) {

        Object usuarioSesionId =
                session.getAttribute("usuarioId");

        Object rolSesion =
                session.getAttribute("rol");

        if (usuarioSesionId == null) {
            return "redirect:/login";
        }

        if (!"ADMIN".equals(String.valueOf(rolSesion))) {
            return "acceso-denegado";
        }

        Optional<Usuario> usuarioEncontrado =
                usuarioRepository.findById(usuarioId);

        if (usuarioEncontrado.isEmpty()) {
            return "redirect:/admin/usuarios";
        }

        Usuario usuario =
                usuarioEncontrado.get();

        if (Rol.PROVEEDOR.equals(nuevoRol)
                && usuario.getEmpresa() == null) {

            model.addAttribute(
                    "errorRol",
                    "No se puede asignar el rol PROVEEDOR porque el usuario no tiene una empresa asociada."
            );

            cargarDatosUsuarios(model);

            return "admin-usuarios";
        }

        usuario.setRol(nuevoRol);

        usuarioRepository.save(usuario);

        return "redirect:/admin/usuarios";
    }

    @PostMapping("/admin/usuarios/eliminar")
    public String eliminarUsuario(
            HttpSession session,
            @RequestParam("usuarioId") Long usuarioId) {

        Object usuarioSesionId =
                session.getAttribute("usuarioId");

        Object rolSesion =
                session.getAttribute("rol");

        if (usuarioSesionId == null) {
            return "redirect:/login";
        }

        if (!"ADMIN".equals(String.valueOf(rolSesion))) {
            return "acceso-denegado";
        }

        Long idAdministrador =
                Long.valueOf(usuarioSesionId.toString());

        if (idAdministrador.equals(usuarioId)) {
            return "redirect:/admin/usuarios";
        }

        if (!usuarioRepository.existsById(usuarioId)) {
            return "redirect:/admin/usuarios";
        }

        usuarioRepository.deleteById(usuarioId);

        return "redirect:/admin/usuarios";
    }

    private void cargarDatosUsuarios(Model model) {

        List<Usuario> usuarios =
                usuarioRepository.findAll();

        model.addAttribute(
                "usuarios",
                usuarios
        );

        model.addAttribute(
                "roles",
                Rol.values()
        );
    }
}