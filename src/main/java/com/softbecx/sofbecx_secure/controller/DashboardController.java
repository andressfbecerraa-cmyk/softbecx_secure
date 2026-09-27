package com.softbecx.sofbecx_secure.controller;

import com.softbecx.sofbecx_secure.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService =
                dashboardService;
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(
            HttpSession session,
            Model model) {

        Object usuarioId =
                session.getAttribute("usuarioId");

        Object rol =
                session.getAttribute("rol");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        DashboardService.DashboardData datos =
                dashboardService.obtenerDatos();

        model.addAttribute(
                "usuarioId",
                usuarioId
        );

        model.addAttribute(
                "rol",
                rol
        );

        model.addAttribute(
                "totalFacturas",
                datos.getTotalFacturas()
        );

        model.addAttribute(
                "facturasPendientes",
                datos.getFacturasPendientes()
        );

        model.addAttribute(
                "facturasAprobadas",
                datos.getFacturasAprobadas()
        );

        model.addAttribute(
                "facturasRechazadas",
                datos.getFacturasRechazadas()
        );

        model.addAttribute(
                "facturasPagadas",
                datos.getFacturasPagadas()
        );

        model.addAttribute(
                "totalPagado",
                datos.getTotalPagado()
        );

        return "dashboard";
    }
}