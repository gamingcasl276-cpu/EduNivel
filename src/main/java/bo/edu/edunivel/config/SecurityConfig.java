
package bo.edu.edunivel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/login",
                    "/registro",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico"
                ).permitAll()

                .requestMatchers("/admin/**")
                    .hasRole("ADMINISTRADOR")

                .requestMatchers("/estudiante/**")
                    .hasRole("ESTUDIANTE")

                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {

                    boolean esAdministrador =
                        authentication.getAuthorities().stream()
                            .anyMatch(authority ->
                                authority.getAuthority()
                                    .equals("ROLE_ADMINISTRADOR")
                            );

                    if (esAdministrador) {
                        response.sendRedirect("/admin");
                    } else {
                        response.sendRedirect("/estudiante");
                    }
                })
                .failureUrl("/login?error")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
