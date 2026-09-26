package com.softbecx.sofbecx_secure.repository;

import com.softbecx.sofbecx_secure.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCorreo(String correo);

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByEmpresaId(Long empresaId);
}