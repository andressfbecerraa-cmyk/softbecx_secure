package com.softbecx.sofbecx_secure.service;

import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public LoginService(
            UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Usuario> autenticar(String correo, String password) {

        Optional<Usuario> usuarioEncontrado =
                usuarioRepository.findByCorreo(correo);

        if (usuarioEncontrado.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioEncontrado.get();

        boolean passwordCorrecta =
                passwordEncoder.matches(
                        password,
                        usuario.getPassword()
                );

        if (!passwordCorrecta) {
            return Optional.empty();
        }

        return Optional.of(usuario);
    }
}