package com.softbecx.sofbecx_secure.controller;

import com.softbecx.sofbecx_secure.model.Empresa;
import com.softbecx.sofbecx_secure.repository.EmpresaRepository;
import com.softbecx.sofbecx_secure.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class EmpresaController {

    private final EmpresaRepository empresaRepository;

    private final UsuarioRepository usuarioRepository;

    public EmpresaController(
            EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository) {

        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/registro-empresa")
    public String mostrarRegistroEmpresa(
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
                "empresa",
                new Empresa()
        );

        return "registro-empresa";
    }

    @PostMapping("/registro-empresa")
    public String registrarEmpresa(
            HttpSession session,
            @Valid @ModelAttribute("empresa") Empresa empresa,
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
            return "registro-empresa";
        }

        if (empresaRepository.existsByNit(empresa.getNit())) {

            model.addAttribute(
                    "nitDuplicado",
                    "El NIT ya está registrado"
            );

            return "registro-empresa";
        }

        empresaRepository.save(empresa);

        return "redirect:/registro-empresa";
    }

    @GetMapping("/admin/empresas")
    public String mostrarEmpresas(
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

        cargarEmpresas(model);

        return "admin-empresas";
    }

    @GetMapping("/admin/empresas/editar/{id}")
    public String mostrarEditarEmpresa(
            @PathVariable Long id,
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

        Optional<Empresa> empresaEncontrada =
                empresaRepository.findById(id);

        if (empresaEncontrada.isEmpty()) {
            return "redirect:/admin/empresas";
        }

        model.addAttribute(
                "empresa",
                empresaEncontrada.get()
        );

        return "editar-empresa";
    }

    @PostMapping("/admin/empresas/editar/{id}")
    public String editarEmpresa(
            @PathVariable Long id,
            @Valid @ModelAttribute("empresa") Empresa empresa,
            BindingResult result,
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

        if (result.hasErrors()) {
            return "editar-empresa";
        }

        Optional<Empresa> empresaEncontrada =
                empresaRepository.findById(id);

        if (empresaEncontrada.isEmpty()) {
            return "redirect:/admin/empresas";
        }

        Empresa empresaExistente =
                empresaEncontrada.get();

        if (!empresaExistente.getNit().equals(empresa.getNit())
                && empresaRepository.existsByNit(empresa.getNit())) {

            model.addAttribute(
                    "nitDuplicado",
                    "El NIT ya está registrado"
            );

            return "editar-empresa";
        }

        empresaExistente.setNombre(
                empresa.getNombre()
        );

        empresaExistente.setNit(
                empresa.getNit()
        );

        empresaExistente.setTelefono(
                empresa.getTelefono()
        );

        empresaExistente.setCorreo(
                empresa.getCorreo()
        );

        empresaExistente.setDireccion(
                empresa.getDireccion()
        );

        empresaRepository.save(empresaExistente);

        return "redirect:/admin/empresas";
    }

    @PostMapping("/admin/empresas/eliminar/{id}")
    public String eliminarEmpresa(
            @PathVariable Long id,
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

        if (!empresaRepository.existsById(id)) {
            return "redirect:/admin/empresas";
        }

        if (usuarioRepository.existsByEmpresaId(id)) {

            model.addAttribute(
                    "empresaEnUso",
                    "No se puede eliminar esta empresa porque tiene usuarios asociados."
            );

            cargarEmpresas(model);

            return "admin-empresas";
        }

        empresaRepository.deleteById(id);

        return "redirect:/admin/empresas";
    }

    private void cargarEmpresas(Model model) {

        List<Empresa> empresas =
                empresaRepository.findAll();

        model.addAttribute(
                "empresas",
                empresas
        );
    }
}