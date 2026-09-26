package com.softbecx.sofbecx_secure.controller;

import com.softbecx.sofbecx_secure.model.EstadoFactura;
import com.softbecx.sofbecx_secure.model.Factura;
import com.softbecx.sofbecx_secure.model.ResultadoValidacion;
import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.repository.FacturaRepository;
import com.softbecx.sofbecx_secure.repository.UsuarioRepository;
import com.softbecx.sofbecx_secure.service.ValidacionPagoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class ValidacionPagoController {

    private final ValidacionPagoService validacionPagoService;

    private final FacturaRepository facturaRepository;

    private final UsuarioRepository usuarioRepository;

    public ValidacionPagoController(
            ValidacionPagoService validacionPagoService,
            FacturaRepository facturaRepository,
            UsuarioRepository usuarioRepository) {

        this.validacionPagoService =
                validacionPagoService;

        this.facturaRepository =
                facturaRepository;

        this.usuarioRepository =
                usuarioRepository;
    }

    @GetMapping("/admin/validaciones-pago")
    public String mostrarValidaciones(
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

        List<Factura> facturas =
                facturaRepository.findAll()
                        .stream()
                        .filter(factura ->
                                factura.getEstado()
                                        == EstadoFactura.APROBADA)
                        .toList();

        model.addAttribute(
                "facturas",
                facturas
        );

        model.addAttribute(
                "resultados",
                ResultadoValidacion.values()
        );

        return "validacion-pago";
    }

    @PostMapping("/admin/validaciones-pago")
    public String validarPago(
            HttpSession session,
            @RequestParam("facturaId") Long facturaId,
            @RequestParam("resultado") ResultadoValidacion resultado,
            @RequestParam("observacion") String observacion,
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
                facturaRepository.findById(facturaId);

        if (facturaEncontrada.isEmpty()) {

            cargarFacturasAprobadas(model);

            model.addAttribute(
                    "errorValidacion",
                    "La factura seleccionada no existe."
            );

            return "validacion-pago";
        }

        Optional<Usuario> usuarioEncontrado =
                usuarioRepository.findById(
                        Long.valueOf(usuarioId.toString())
                );

        if (usuarioEncontrado.isEmpty()) {

            return "redirect:/login";
        }

        boolean validacionRealizada =
                validacionPagoService.validarFactura(
                        facturaEncontrada.get(),
                        usuarioEncontrado.get(),
                        resultado,
                        observacion
                );

        if (!validacionRealizada) {

            cargarFacturasAprobadas(model);

            model.addAttribute(
                    "errorValidacion",
                    "No se puede validar esta factura. Verifique que esté aprobada y que la observación esté diligenciada."
            );

            return "validacion-pago";
        }

        return "redirect:/admin/validaciones-pago";
    }

    private void cargarFacturasAprobadas(Model model) {

        List<Factura> facturas =
                facturaRepository.findAll()
                        .stream()
                        .filter(factura ->
                                factura.getEstado()
                                        == EstadoFactura.APROBADA)
                        .toList();

        model.addAttribute(
                "facturas",
                facturas
        );

        model.addAttribute(
                "resultados",
                ResultadoValidacion.values()
        );
    }
}