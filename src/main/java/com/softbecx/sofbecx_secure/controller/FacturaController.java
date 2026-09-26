package com.softbecx.sofbecx_secure.controller;

import com.softbecx.sofbecx_secure.model.Empresa;
import com.softbecx.sofbecx_secure.model.EstadoFactura;
import com.softbecx.sofbecx_secure.model.Factura;
import com.softbecx.sofbecx_secure.model.Rol;
import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.repository.EmpresaRepository;
import com.softbecx.sofbecx_secure.repository.UsuarioRepository;
import com.softbecx.sofbecx_secure.service.FacturaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FacturaController {

    private final FacturaService facturaService;

    private final EmpresaRepository empresaRepository;

    private final UsuarioRepository usuarioRepository;

    public FacturaController(
            FacturaService facturaService,
            EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository) {

        this.facturaService = facturaService;

        this.empresaRepository = empresaRepository;

        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/admin/facturas")
    public String mostrarFacturas(
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
                facturaService.listarFacturas();

        model.addAttribute(
                "facturas",
                facturas
        );

        model.addAttribute(
                "estados",
                EstadoFactura.values()
        );

        return "admin-facturas";
    }

    @GetMapping("/admin/facturas/nueva")
    public String mostrarFormularioFactura(
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

        model.addAttribute(
                "factura",
                new Factura()
        );

        cargarDatosFormulario(model);

        return "registro-factura";
    }

    @PostMapping("/admin/facturas")
    public String registrarFactura(
            HttpSession session,
            @Valid @ModelAttribute("factura") Factura factura,
            BindingResult result,
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

        if (result.hasErrors()) {

            cargarDatosFormulario(model);

            return "registro-factura";
        }

        facturaService.guardarFactura(factura);

        return "redirect:/admin/facturas";
    }

    @PostMapping("/admin/facturas/estado")
    public String cambiarEstadoFactura(
            HttpSession session,
            @RequestParam("facturaId") Long facturaId,
            @RequestParam("estado") EstadoFactura nuevoEstado,
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

        boolean cambioRealizado =
                facturaService.cambiarEstado(
                        facturaId,
                        nuevoEstado
                );

        if (!cambioRealizado) {

            if (nuevoEstado == EstadoFactura.PAGADA) {

                model.addAttribute(
                        "errorEstado",
                        "La factura no puede marcarse como pagada porque no tiene una validación de pago aprobada."
                );

            } else {

                model.addAttribute(
                        "errorEstado",
                        "No se puede realizar ese cambio de estado."
                );
            }
        }

        cargarDatosFacturas(model);

        return "admin-facturas";
    }

    private void cargarDatosFacturas(Model model) {

        List<Factura> facturas =
                facturaService.listarFacturas();

        model.addAttribute(
                "facturas",
                facturas
        );

        model.addAttribute(
                "estados",
                EstadoFactura.values()
        );
    }

    private void cargarDatosFormulario(Model model) {

        List<Empresa> empresas =
                empresaRepository.findAll();

        List<Usuario> proveedores =
                usuarioRepository.findAll()
                        .stream()
                        .filter(usuario ->
                                Rol.PROVEEDOR.equals(usuario.getRol()))
                        .toList();

        model.addAttribute(
                "empresas",
                empresas
        );

        model.addAttribute(
                "proveedores",
                proveedores
        );
    }
}