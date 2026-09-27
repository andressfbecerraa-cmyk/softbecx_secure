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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProgramacionPagoService {

    private final ProgramacionPagoRepository
            programacionPagoRepository;

    private final ValidacionPagoRepository
            validacionPagoRepository;

    private final FacturaService facturaService;

    public ProgramacionPagoService(
            ProgramacionPagoRepository
                    programacionPagoRepository,
            ValidacionPagoRepository
                    validacionPagoRepository,
            FacturaService facturaService) {

        this.programacionPagoRepository =
                programacionPagoRepository;

        this.validacionPagoRepository =
                validacionPagoRepository;

        this.facturaService =
                facturaService;
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

    @Transactional
    public boolean ejecutarPago(Long programacionId) {

        if (programacionId == null) {
            return false;
        }

        Optional<ProgramacionPago>
                programacionEncontrada =
                programacionPagoRepository
                        .findById(programacionId);

        if (programacionEncontrada.isEmpty()) {
            return false;
        }

        ProgramacionPago programacion =
                programacionEncontrada.get();

        if (programacion.getEstado()
                != EstadoProgramacionPago.PROGRAMADO) {

            return false;
        }

        Factura factura =
                programacion.getFactura();

        if (factura == null) {
            return false;
        }

        boolean facturaPagada =
                facturaService.marcarComoPagada(
                        factura
                );

        if (!facturaPagada) {
            return false;
        }

        programacion.setEstado(
                EstadoProgramacionPago.EJECUTADO
        );

        programacion.setFechaEjecucion(
                LocalDateTime.now()
        );

        programacionPagoRepository.save(
                programacion
        );

        return true;
    }

    public List<ProgramacionPago>
    listarProgramaciones() {

        return programacionPagoRepository
                .findAll();
    }
}