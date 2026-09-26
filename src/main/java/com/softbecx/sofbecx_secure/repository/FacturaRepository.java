package com.softbecx.sofbecx_secure.repository;

import com.softbecx.sofbecx_secure.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository
        extends JpaRepository<Factura, Long> {
}