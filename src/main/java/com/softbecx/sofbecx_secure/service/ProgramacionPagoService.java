package com.softbecx.sofbecx_secure.service;

import com.softbecx.sofbecx_secure.model.EstadoFactura;
import com.softbecx.sofbecx_secure.model.EstadoProgramacionPago;
import com.softbecx.sofbecx_secure.model.Factura;
import com.softbecx.sofbecx_secure.model.ProgramacionPago;
import com.softbecx.sofbecx_secure.model.ResultadoValidacion;
import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.repository.ProgramacionPagoRepository;
import com.softbecx.sofbecx_secure.repository.ValidacionPagoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProgramacionPagoService {

    private final ProgramacionPagoRepository
            programacionPagoRepository;

    private final ValidacionPagoRepository
            validacionPagoRepository;

    public ProgramacionPagoService(
            ProgramacionPagoRepository
                    programacionPagoRepository,
            ValidacionPagoRepository
                    validacionPagoRepository) {

        this.programacionPagoRepository =
                programacionPagoRepository;

        this.validacionPagoRepository =
                validacionPagoRepository;
    }

    public boolean programarPago(
            Factura factura,
            Usuario usuario,
            LocalDate fechaProgramada,
            String observacion) {

        if (factura == null) {
            return false;
        }

        if (usuario == null) {
            return false;
        }

        if (fechaProgramada == null) {
            return false;
        }

        if (fechaProgramada.isBefore(LocalDate.now())) {
            return false;
        }

        if (observacion == null
                || observacion.isBlank()) {

            return false;
        }

        if (factura.getEstado()
                != EstadoFactura.APROBADA) {

            return false;
        }

        boolean validacionAprobada =
                validacionPagoRepository
                        .existsByFacturaIdAndResultado(
                                factura.getId(),
                                ResultadoValidacion.APROBADA
                        );

        if (!validacionAprobada) {
            return false;
        }

        boolean yaEstaProgramada =
                programacionPagoRepository
                        .existsByFacturaId(
                                factura.getId()
                        );

        if (yaEstaProgramada) {
            return false;
        }

        ProgramacionPago programacionPago =
                new ProgramacionPago();

        programacionPago.setFechaProgramada(
                fechaProgramada
        );

        programacionPago.setEstado(
                EstadoProgramacionPago.PROGRAMADO
        );

        programacionPago.setObservacion(
                observacion
        );

        programacionPago.setFactura(
                factura
        );

        programacionPago.setUsuario(
                usuario
        );

        programacionPagoRepository.save(
                programacionPago
        );

        return true;
    }

    public List<ProgramacionPago>
    listarProgramaciones() {

        return programacionPagoRepository
                .findAll();
    }
}