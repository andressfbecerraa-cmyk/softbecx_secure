package com.softbecx.sofbecx_secure.controller;

import com.softbecx.sofbecx_secure.model.EstadoFactura;
import com.softbecx.sofbecx_secure.model.Factura;
import com.softbecx.sofbecx_secure.service.FacturaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class ComprobantePagoController {

    private final FacturaService facturaService;

    public ComprobantePagoController(
            FacturaService facturaService) {

        this.facturaService =
                facturaService;
    }

    @GetMapping("/admin/comprobante-pago/{facturaId}")
    public String mostrarComprobante(
            @PathVariable Long facturaId,
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

        Optional<Factura> facturaEncontrada =
                facturaService.buscarPorId(facturaId);

        if (facturaEncontrada.isEmpty()) {

            model.addAttribute(
                    "error",
                    "La factura no existe."
            );

            return "comprobante-pago";
        }

        Factura factura =
                facturaEncontrada.get();

        if (factura.getEstado()
                != EstadoFactura.PAGADA) {

            model.addAttribute(
                    "error",
                    "No se puede generar el comprobante porque la factura todavía no está pagada."
            );

            return "comprobante-pago";
        }

        model.addAttribute(
                "factura",
                factura
        );

        return "comprobante-pago";
    }
}