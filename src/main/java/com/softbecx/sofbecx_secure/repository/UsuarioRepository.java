package com.softbecx.sofbecx_secure.repository;

import com.softbecx.sofbecx_secure.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCorreo(String correo);
}