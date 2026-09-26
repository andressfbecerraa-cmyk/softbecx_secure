package com.softbecx.sofbecx_secure.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String mostrarDashboard(
            HttpSession session,
            Model model) {

        Object usuarioId = session.getAttribute("usuarioId");
        Object rol = session.getAttribute("rol");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("rol", rol);

        return "dashboard";
    }
}