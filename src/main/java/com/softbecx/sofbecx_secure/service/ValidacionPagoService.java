package com.softbecx.sofbecx_secure.service;

import com.softbecx.sofbecx_secure.model.AccionAuditoria;
import com.softbecx.sofbecx_secure.model.EstadoFactura;
import com.softbecx.sofbecx_secure.model.Factura;
import com.softbecx.sofbecx_secure.model.ResultadoValidacion;
import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.model.ValidacionPago;
import com.softbecx.sofbecx_secure.repository.ValidacionPagoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ValidacionPagoService {

    private final ValidacionPagoRepository validacionPagoRepository;

    private final AuditoriaService auditoriaService;

    public ValidacionPagoService(
            ValidacionPagoRepository validacionPagoRepository,
            AuditoriaService auditoriaService) {

        this.validacionPagoRepository =
                validacionPagoRepository;

        this.auditoriaService =
                auditoriaService;
    }

    public boolean validarFactura(
            Factura factura,
            Usuario usuario,
            ResultadoValidacion resultado,
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

        if (factura.getEstado() != EstadoFactura.APROBADA) {
            return false;
        }

        ValidacionPago validacionPago =
                new ValidacionPago();

        validacionPago.setFechaValidacion(
                LocalDateTime.now()
        );

        validacionPago.setResultado(
                resultado
        );

        validacionPago.setObservacion(
                observacion
        );

        validacionPago.setFactura(
                factura
        );

        validacionPago.setUsuario(
                usuario
        );

        validacionPagoRepository.save(
                validacionPago
        );

        String descripcion;

        if (resultado == ResultadoValidacion.APROBADA) {

            descripcion =
                    "Validación de pago aprobada. Observación: "
                            + observacion;

        } else {

            descripcion =
                    "Validación de pago rechazada. Observación: "
                            + observacion;
        }

        auditoriaService.registrar(
                AccionAuditoria.VALIDACION_PAGO,
                descripcion,
                usuario,
                factura
        );

        return true;
    }

    public List<ValidacionPago> listarValidaciones() {

        return validacionPagoRepository.findAll();
    }
}