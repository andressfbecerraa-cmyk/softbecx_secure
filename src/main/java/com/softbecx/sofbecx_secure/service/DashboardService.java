package com.softbecx.sofbecx_secure.service;

import com.softbecx.sofbecx_secure.model.EstadoFactura;
import com.softbecx.sofbecx_secure.model.Factura;
import com.softbecx.sofbecx_secure.repository.FacturaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    private final FacturaRepository facturaRepository;

    public DashboardService(
            FacturaRepository facturaRepository) {

        this.facturaRepository =
                facturaRepository;
    }

    public DashboardData obtenerDatos() {

        List<Factura> facturas =
                facturaRepository.findAll();

        long totalFacturas =
                facturas.size();

        long facturasPendientes =
                contarPorEstado(
                        facturas,
                        EstadoFactura.PENDIENTE
                );

        long facturasAprobadas =
                contarPorEstado(
                        facturas,
                        EstadoFactura.APROBADA
                );

        long facturasRechazadas =
                contarPorEstado(
                        facturas,
                        EstadoFactura.RECHAZADA
                );

        long facturasPagadas =
                contarPorEstado(
                        facturas,
                        EstadoFactura.PAGADA
                );

        BigDecimal totalPagado =
                calcularTotalPagado(facturas);

        return new DashboardData(
                totalFacturas,
                facturasPendientes,
                facturasAprobadas,
                facturasRechazadas,
                facturasPagadas,
                totalPagado
        );
    }

    private long contarPorEstado(
            List<Factura> facturas,
            EstadoFactura estado) {

        return facturas.stream()
                .filter(factura ->
                        factura.getEstado() == estado)
                .count();
    }

    private BigDecimal calcularTotalPagado(
            List<Factura> facturas) {

        return facturas.stream()
                .filter(factura ->
                        factura.getEstado()
                                == EstadoFactura.PAGADA)
                .map(Factura::getValor)
                .filter(valor -> valor != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    public static class DashboardData {

        private final long totalFacturas;

        private final long facturasPendientes;

        private final long facturasAprobadas;

        private final long facturasRechazadas;

        private final long facturasPagadas;

        private final BigDecimal totalPagado;

        public DashboardData(
                long totalFacturas,
                long facturasPendientes,
                long facturasAprobadas,
                long facturasRechazadas,
                long facturasPagadas,
                BigDecimal totalPagado) {

            this.totalFacturas =
                    totalFacturas;

            this.facturasPendientes =
                    facturasPendientes;

            this.facturasAprobadas =
                    facturasAprobadas;

            this.facturasRechazadas =
                    facturasRechazadas;

            this.facturasPagadas =
                    facturasPagadas;

            this.totalPagado =
                    totalPagado;
        }

        public long getTotalFacturas() {
            return totalFacturas;
        }

        public long getFacturasPendientes() {
            return facturasPendientes;
        }

        public long getFacturasAprobadas() {
            return facturasAprobadas;
        }

        public long getFacturasRechazadas() {
            return facturasRechazadas;
        }

        public long getFacturasPagadas() {
            return facturasPagadas;
        }

        public BigDecimal getTotalPagado() {
            return totalPagado;
        }
    }
}