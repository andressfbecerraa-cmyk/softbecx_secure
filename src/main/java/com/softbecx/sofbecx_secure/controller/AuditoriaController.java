package com.softbecx.sofbecx_secure.controller;

import com.softbecx.sofbecx_secure.model.Auditoria;
import com.softbecx.sofbecx_secure.service.AuditoriaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(
            AuditoriaService auditoriaService) {

        this.auditoriaService =
                auditoriaService;
    }

    @GetMapping("/admin/auditoria")
    public String mostrarAuditoria(
            HttpSession session,
            Model model) {

        Object usuarioId =
                session.getAttribute("usuarioId");

        Object rol =
                session.getAttribute("rol");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        if (!"ADMIN".equals(String.valueOf(rol))) {
            return "acceso-denegado";
        }

        List<Auditoria> auditorias =
                auditoriaService.listarAuditorias();

        model.addAttribute(
                "auditorias",
                auditorias
        );

        return "auditoria";
    }
}