package com.softbecx.sofbecx_secure.controller;

import com.softbecx.sofbecx_secure.model.EstadoFactura;
import com.softbecx.sofbecx_secure.model.Factura;
import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.repository.FacturaRepository;
import com.softbecx.sofbecx_secure.repository.UsuarioRepository;
import com.softbecx.sofbecx_secure.repository.ValidacionPagoRepository;
import com.softbecx.sofbecx_secure.service.ProgramacionPagoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class ProgramacionPagoController {

    private final ProgramacionPagoService
            programacionPagoService;

    private final FacturaRepository facturaRepository;

    private final UsuarioRepository usuarioRepository;

    private final ValidacionPagoRepository
            validacionPagoRepository;

    public ProgramacionPagoController(
            ProgramacionPagoService programacionPagoService,
            FacturaRepository facturaRepository,
            UsuarioRepository usuarioRepository,
            ValidacionPagoRepository validacionPagoRepository) {

        this.programacionPagoService =
                programacionPagoService;

        this.facturaRepository =
                facturaRepository;

        this.usuarioRepository =
                usuarioRepository;

        this.validacionPagoRepository =
                validacionPagoRepository;
    }

    @GetMapping("/admin/programaciones-pago")
    public String mostrarProgramaciones(
            HttpSession session,
            Model model) {

        Object usuarioId =
                session.getAttribute("usuarioId");

        Object rol =
                session.getAttribute("rol");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        if (!puedeProgramarPago(rol)) {
            return "acceso-denegado";
        }

        cargarDatos(model);

        return "programacion-pagos";
    }

    @PostMapping("/admin/programaciones-pago")
    public String programarPago(
            HttpSession session,
            @RequestParam("facturaId") Long facturaId,
            @RequestParam("fechaProgramada")
            LocalDate fechaProgramada,
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

        if (!puedeProgramarPago(rol)) {
            return "acceso-denegado";
        }

        Optional<Factura> facturaEncontrada =
                facturaRepository.findById(facturaId);

        if (facturaEncontrada.isEmpty()) {

            cargarDatos(model);

            model.addAttribute(
                    "errorProgramacion",
                    "La factura seleccionada no existe."
            );

            return "programacion-pagos";
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

        boolean programacionRealizada =
                programacionPagoService.programarPago(
                        facturaEncontrada.get(),
                        usuarioEncontrado.get(),
                        fechaProgramada,
                        observacion
                );

        if (!programacionRealizada) {

            cargarDatos(model);

            model.addAttribute(
                    "errorProgramacion",
                    "No se puede programar este pago. Verifique que la factura esté aprobada, tenga una validación de pago aprobada y que los datos estén completos."
            );

            return "programacion-pagos";
        }

        return "redirect:/admin/programaciones-pago";
    }

    private boolean puedeProgramarPago(Object rol) {

        return "ADMIN".equals(String.valueOf(rol))
                || "TESORERO".equals(
                String.valueOf(rol)
        );
    }

    private void cargarDatos(Model model) {

        List<Factura> facturas =
                facturaRepository.findAll()
                        .stream()
                        .filter(factura ->
                                factura.getEstado()
                                        == EstadoFactura.APROBADA)
                        .filter(factura ->
                                factura.getId() != null
                                        && tieneValidacionAprobada(
                                        factura.getId()
                                ))
                        .toList();

        model.addAttribute(
                "facturas",
                facturas
        );

        model.addAttribute(
                "programaciones",
                programacionPagoService
                        .listarProgramaciones()
        );
    }

    private boolean tieneValidacionAprobada(
            Long facturaId) {

        return validacionPagoRepository
                .existsByFacturaIdAndResultado(
                        facturaId,
                        com.softbecx.sofbecx_secure.model.ResultadoValidacion.APROBADA
                );
    }
}