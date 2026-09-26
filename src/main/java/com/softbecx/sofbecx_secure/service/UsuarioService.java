package com.softbecx.sofbecx_secure.service;

import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario guardarUsuario(Usuario usuario) {

        String passwordHasheada =
                passwordEncoder.encode(usuario.getPassword());

        usuario.setPassword(passwordHasheada);

        return usuarioRepository.save(usuario);
    }

    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }
}