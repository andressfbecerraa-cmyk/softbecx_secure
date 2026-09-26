package com.softbecx.sofbecx_secure.repository;

import com.softbecx.sofbecx_secure.model.ProgramacionPago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramacionPagoRepository
        extends JpaRepository<ProgramacionPago, Long> {

    boolean existsByFacturaId(Long facturaId);
}