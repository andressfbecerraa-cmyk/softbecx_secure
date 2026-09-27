package com.softbecx.sofbecx_secure.service;

import com.softbecx.sofbecx_secure.model.AccionAuditoria;
import com.softbecx.sofbecx_secure.model.Auditoria;
import com.softbecx.sofbecx_secure.model.Factura;
import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(
            AuditoriaRepository auditoriaRepository) {

        this.auditoriaRepository =
                auditoriaRepository;
    }

    public Auditoria registrar(
            AccionAuditoria accion,
            String descripcion,
            Usuario usuario,
            Factura factura) {

        Auditoria auditoria =
                new Auditoria();

        auditoria.setFecha(
                LocalDateTime.now()
        );

        auditoria.setAccion(
                accion
        );

        auditoria.setDescripcion(
                descripcion
        );

        auditoria.setUsuario(
                usuario
        );

        auditoria.setFactura(
                factura
        );

        return auditoriaRepository.save(
                auditoria
        );
    }

    public List<Auditoria> listarAuditorias() {

        return auditoriaRepository.findAll();
    }
}