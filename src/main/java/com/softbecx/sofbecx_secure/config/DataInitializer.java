package com.softbecx.sofbecx_secure.config;

import com.softbecx.sofbecx_secure.model.Rol;
import com.softbecx.sofbecx_secure.model.Usuario;
import com.softbecx.sofbecx_secure.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(
            UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        String correoAdmin = "admin@softbecx.com";

        if (usuarioRepository.existsByCorreo(correoAdmin)) {
            return;
        }

        Usuario administrador = new Usuario();

        administrador.setNombre("Administrador");
        administrador.setApellido("SoftBecX");
        administrador.setCorreo(correoAdmin);

        administrador.setPassword(
                passwordEncoder.encode("Admin12345")
        );

        administrador.setRol(Rol.ADMIN);

        usuarioRepository.save(administrador);
    }
}