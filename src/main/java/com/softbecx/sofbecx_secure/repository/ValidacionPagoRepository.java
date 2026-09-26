package com.softbecx.sofbecx_secure.repository;

import com.softbecx.sofbecx_secure.model.ResultadoValidacion;
import com.softbecx.sofbecx_secure.model.ValidacionPago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidacionPagoRepository
        extends JpaRepository<ValidacionPago, Long> {

    boolean existsByFacturaIdAndResultado(
            Long facturaId,
            ResultadoValidacion resultado
    );
}