package com.softbecx.sofbecx_secure.controller;

import com.softbecx.sofbecx_secure.model.EstadoFactura;
import com.softbecx.sofbecx_secure.model.Factura;
import com.softbecx.sofbecx_secure.model.ResultadoAprobacion;
import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.repository.FacturaRepository;
import com.softbecx.sofbecx_secure.repository.UsuarioRepository;
import com.softbecx.sofbecx_secure.service.AprobacionFacturaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class AprobacionFacturaController {

    private final AprobacionFacturaService
            aprobacionFacturaService;

    private final FacturaRepository facturaRepository;

    private final UsuarioRepository usuarioRepository;

    public AprobacionFacturaController(
            AprobacionFacturaService
                    aprobacionFacturaService,
            FacturaRepository facturaRepository,
            UsuarioRepository usuarioRepository) {

        this.aprobacionFacturaService =
                aprobacionFacturaService;

        this.facturaRepository =
                facturaRepository;

        this.usuarioRepository =
                usuarioRepository;
    }

    @GetMapping("/admin/aprobaciones-facturas")
    public String mostrarAprobaciones(
            HttpSession session,
            Model model) {

        Object usuarioId =
                session.getAttribute("usuarioId");

        Object rol =
                session.getAttribute("rol");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        if (!puedeAprobar(rol)) {
            return "acceso-denegado";
        }

        cargarFacturasPendientes(model);

        model.addAttribute(
                "resultados",
                ResultadoAprobacion.values()
        );

        return "aprobacion-facturas";
    }

    @PostMapping("/admin/aprobaciones-facturas")
    public String registrarAprobacion(
            HttpSession session,
            @RequestParam("facturaId") Long facturaId,
            @RequestParam("resultado")
            ResultadoAprobacion resultado,
            @RequestParam("observacion")
            String observacion,
            Model model) {

        Object usuarioId =
                session.getAttribute("usuarioId");

        Object rol =
                session.getAttribute("rol");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        if (!puedeAprobar(rol)) {
            return "acceso-denegado";
        }

        Optional<Factura> facturaEncontrada =
                facturaRepository.findById(facturaId);

        if (facturaEncontrada.isEmpty()) {

            cargarDatosError(
                    model,
                    "La factura seleccionada no existe."
            );

            return "aprobacion-facturas";
        }

        Optional<Usuario> usuarioEncontrado =
                usuarioRepository.findById(
                        Long.valueOf(
                                usuarioId.toString()
                        )
                );

        if (usuarioEncontrado.isEmpty()) {
            return "redirect:/login";
        }

        boolean aprobacionRealizada =
                aprobacionFacturaService
                        .aprobarFactura(
                                facturaEncontrada.get(),
                                usuarioEncontrado.get(),
                                resultado,
                                observacion
                        );

        if (!aprobacionRealizada) {

            cargarDatosError(
                    model,
                    "No se puede procesar esta factura. Verifique que esté pendiente y que la observación esté diligenciada."
            );

            return "aprobacion-facturas";
        }

        Factura factura =
                facturaEncontrada.get();

        if (resultado
                == ResultadoAprobacion.APROBADA) {

            factura.setEstado(
                    EstadoFactura.APROBADA
            );

        } else {

            factura.setEstado(
                    EstadoFactura.RECHAZADA
            );
        }

        facturaRepository.save(factura);

        return "redirect:/admin/aprobaciones-facturas";
    }

    private boolean puedeAprobar(Object rol) {

        return "ADMIN".equals(String.valueOf(rol))
                || "APROBADOR".equals(
                String.valueOf(rol)
        );
    }

    private void cargarFacturasPendientes(
            Model model) {

        List<Factura> facturas =
                facturaRepository.findAll()
                        .stream()
                        .filter(factura ->
                                factura.getEstado()
                                        == EstadoFactura.PENDIENTE)
                        .toList();

        model.addAttribute(
                "facturas",
                facturas
        );
    }

    private void cargarDatosError(
            Model model,
            String mensaje) {

        cargarFacturasPendientes(model);

        model.addAttribute(
                "resultados",
                ResultadoAprobacion.values()
        );

        model.addAttribute(
                "errorAprobacion",
                mensaje
        );
    }
}