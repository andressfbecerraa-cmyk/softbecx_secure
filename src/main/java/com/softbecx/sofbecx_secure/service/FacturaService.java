package com.softbecx.sofbecx_secure.service;

import com.softbecx.sofbecx_secure.model.AccionAuditoria;
import com.softbecx.sofbecx_secure.model.EstadoFactura;
import com.softbecx.sofbecx_secure.model.Factura;
import com.softbecx.sofbecx_secure.model.ResultadoValidacion;
import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.repository.FacturaRepository;
import com.softbecx.sofbecx_secure.repository.ValidacionPagoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    private final ValidacionPagoRepository validacionPagoRepository;

    private final AuditoriaService auditoriaService;

    public FacturaService(
            FacturaRepository facturaRepository,
            ValidacionPagoRepository validacionPagoRepository,
            AuditoriaService auditoriaService) {

        this.facturaRepository =
                facturaRepository;

        this.validacionPagoRepository =
                validacionPagoRepository;

        this.auditoriaService =
                auditoriaService;
    }

    public Factura guardarFactura(
            Factura factura,
            Usuario usuario) {

        factura.setEstado(
                EstadoFactura.PENDIENTE
        );

        Factura facturaGuardada =
                facturaRepository.save(factura);

        auditoriaService.registrar(
                AccionAuditoria.CREACION_FACTURA,
                "Factura creada correctamente.",
                usuario,
                facturaGuardada
        );

        return facturaGuardada;
    }

    public List<Factura> listarFacturas() {

        return facturaRepository.findAll();
    }

    public List<Factura> listarFacturasPorProveedor(
            Long proveedorId) {

        return facturaRepository
                .findByProveedorId(proveedorId);
    }

    public Optional<Factura> buscarPorId(Long id) {

        return facturaRepository.findById(id);
    }

    public Factura guardarCambios(Factura factura) {

        return facturaRepository.save(factura);
    }

    public boolean cambiarEstado(
            Long facturaId,
            EstadoFactura nuevoEstado) {

        Optional<Factura> facturaEncontrada =
                facturaRepository.findById(facturaId);

        if (facturaEncontrada.isEmpty()) {
            return false;
        }

        Factura factura =
                facturaEncontrada.get();

        EstadoFactura estadoActual =
                factura.getEstado();

        if (nuevoEstado == EstadoFactura.PAGADA) {

            return marcarComoPagada(factura);
        }

        if (!transicionPermitida(
                estadoActual,
                nuevoEstado)) {

            return false;
        }

        factura.setEstado(nuevoEstado);

        facturaRepository.save(factura);

        return true;
    }

    public boolean marcarComoPagada(
            Factura factura) {

        if (factura == null) {
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

        factura.setEstado(
                EstadoFactura.PAGADA
        );

        facturaRepository.save(factura);

        return true;
    }

    private boolean transicionPermitida(
            EstadoFactura estadoActual,
            EstadoFactura nuevoEstado) {

        if (estadoActual == null) {
            return false;
        }

        if (estadoActual == nuevoEstado) {
            return true;
        }

        if (estadoActual == EstadoFactura.PENDIENTE
                && (nuevoEstado == EstadoFactura.APROBADA
                || nuevoEstado == EstadoFactura.RECHAZADA)) {

            return true;
        }

        return false;
    }
}
