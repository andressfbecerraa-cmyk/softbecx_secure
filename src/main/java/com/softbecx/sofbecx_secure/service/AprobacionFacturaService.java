package com.softbecx.sofbecx_secure.service;

import com.softbecx.sofbecx_secure.model.AccionAuditoria;
import com.softbecx.sofbecx_secure.model.AprobacionFactura;
import com.softbecx.sofbecx_secure.model.EstadoFactura;
import com.softbecx.sofbecx_secure.model.Factura;
import com.softbecx.sofbecx_secure.model.ResultadoAprobacion;
import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.repository.AprobacionFacturaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AprobacionFacturaService {

    private final AprobacionFacturaRepository
            aprobacionFacturaRepository;

    private final AuditoriaService auditoriaService;

    public AprobacionFacturaService(
            AprobacionFacturaRepository
                    aprobacionFacturaRepository,
            AuditoriaService auditoriaService) {

        this.aprobacionFacturaRepository =
                aprobacionFacturaRepository;

        this.auditoriaService =
                auditoriaService;
    }

    public boolean aprobarFactura(
            Factura factura,
            Usuario usuario,
            ResultadoAprobacion resultado,
            String observacion) {

        if (factura == null) {
            return false;
        }

        if (usuario == null) {
            return false;
        }

        if (resultado == null) {
            return false;
        }

        if (observacion == null
                || observacion.isBlank()) {

            return false;
        }

        if (factura.getEstado()
                != EstadoFactura.PENDIENTE) {

            return false;
        }

        AprobacionFactura aprobacion =
                new AprobacionFactura();

        aprobacion.setFechaAprobacion(
                LocalDateTime.now()
        );

        aprobacion.setResultado(
                resultado
        );

        aprobacion.setObservacion(
                observacion
        );

        aprobacion.setFactura(
                factura
        );

        aprobacion.setUsuario(
                usuario
        );

        aprobacionFacturaRepository.save(
                aprobacion
        );

        String descripcion;

        if (resultado == ResultadoAprobacion.APROBADA) {

            descripcion =
                    "Factura aprobada. Observación: "
                            + observacion;

        } else {

            descripcion =
                    "Factura rechazada. Observación: "
                            + observacion;
        }

        auditoriaService.registrar(
                AccionAuditoria.APROBACION_FACTURA,
                descripcion,
                usuario,
                factura
        );

        return true;
    }

    public List<AprobacionFactura>
    listarAprobaciones() {

        return aprobacionFacturaRepository
                .findAll();
    }
}