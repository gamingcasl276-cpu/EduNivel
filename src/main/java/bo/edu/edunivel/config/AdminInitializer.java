
package bo.edu.edunivel.config;

import bo.edu.edunivel.model.Rol;
import bo.edu.edunivel.model.Usuario;
import bo.edu.edunivel.repository.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    public AdminInitializer(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            Environment environment) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    @Override
    public void run(String... args) {

        // Evitar crear administradores duplicados
        if (usuarioRepository.existsByRol(Rol.ADMINISTRADOR)) {
            System.out.println(
                    "EduNivel: Ya existe un administrador."
            );
            return;
        }

        String nombreUsuario =
                environment.getProperty("ADMIN_USERNAME");

        String contrasena =
                environment.getProperty("ADMIN_PASSWORD");

        // No crear una cuenta si faltan las credenciales
        if (nombreUsuario == null || nombreUsuario.isBlank()
                || contrasena == null || contrasena.length() < 12) {

            System.out.println(
                    "EduNivel: Configura ADMIN_USERNAME y "
                    + "ADMIN_PASSWORD para crear el administrador inicial."
            );
            return;
        }

        Usuario administrador = new Usuario();

        administrador.setNombreUsuario(
                nombreUsuario.trim().toLowerCase()
        );

        administrador.setContrasena(
                passwordEncoder.encode(contrasena)
        );

        administrador.setRol(Rol.ADMINISTRADOR);
        administrador.setActivo(true);

        usuarioRepository.save(administrador);

        System.out.println(
                "EduNivel: Administrador inicial creado correctamente."
        );
    }
}
