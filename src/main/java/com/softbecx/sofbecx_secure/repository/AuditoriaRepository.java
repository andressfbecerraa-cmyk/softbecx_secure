package com.softbecx.sofbecx_secure.repository;

import com.softbecx.sofbecx_secure.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository
        extends JpaRepository<Auditoria, Long> {
}