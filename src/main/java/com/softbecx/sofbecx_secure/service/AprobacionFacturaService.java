package com.softbecx.sofbecx_secure.service;

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

    public AprobacionFacturaService(
            AprobacionFacturaRepository
                    aprobacionFacturaRepository) {

        this.aprobacionFacturaRepository =
                aprobacionFacturaRepository;
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

        return true;
    }

    public List<AprobacionFactura>
    listarAprobaciones() {

        return aprobacionFacturaRepository
                .findAll();
    }
}