package com.softbecx.sofbecx_secure.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProveedorController {

    @GetMapping("/portal-proveedor")
    public String mostrarPortalProveedor(
            HttpSession session,
            Model model) {

        Object usuarioId = session.getAttribute("usuarioId");
        Object rol = session.getAttribute("rol");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        if (!"PROVEEDOR".equals(rol.toString())) {
            return "acceso-denegado";
        }

        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("rol", rol);

        return "portal-proveedor";
    }
}