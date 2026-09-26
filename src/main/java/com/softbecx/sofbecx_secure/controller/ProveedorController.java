package com.softbecx.sofbecx_secure.controller;

import com.softbecx.sofbecx_secure.model.Factura;
import com.softbecx.sofbecx_secure.service.FacturaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProveedorController {

    private final FacturaService facturaService;

    public ProveedorController(
            FacturaService facturaService) {

        this.facturaService = facturaService;
    }

    @GetMapping("/portal-proveedor")
    public String mostrarPortalProveedor(
            HttpSession session,
            Model model) {

        Object usuarioId =
                session.getAttribute("usuarioId");

        Object rol =
                session.getAttribute("rol");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        if (!"PROVEEDOR".equals(String.valueOf(rol))) {
            return "acceso-denegado";
        }

        Long proveedorId =
                Long.valueOf(usuarioId.toString());

        List<Factura> facturas =
                facturaService
                        .listarFacturasPorProveedor(
                                proveedorId
                        );

        model.addAttribute(
                "usuarioId",
                proveedorId
        );

        model.addAttribute(
                "rol",
                rol
        );

        model.addAttribute(
                "facturas",
                facturas
        );

        return "portal-proveedor";
    }
}
