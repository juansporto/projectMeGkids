package com.projeto.sistemameg2.config;

import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private CustomSuccessHandler successHandler; // Injeção do handler

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/api/auth/**", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("GERENTE")
                        .requestMatchers("/vendedor/**").hasAnyRole("GERENTE", "VENDEDOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .successHandler(successHandler) // Redirecionamento baseado na ROLE
                        .failureUrl("/auth/login?erro")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            String acceptHeader = request.getHeader("Accept");
                            String ajaxHeader = request.getHeader("X-Requested-With");

                            boolean isApi = (acceptHeader != null && acceptHeader.contains("application/json")) ||
                                    "XMLHttpRequest".equals(ajaxHeader);

                            if (isApi) {
                                response.setContentType("application/json");
                                response.setStatus(401);
                                response.getWriter().write("{\"error\": \"Não autorizado\"}");
                            } else {
                                response.sendRedirect("/auth/login");
                            }
                        })
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            Usuario usuario = usuarioRepositorio.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            return User.builder()
                    .username(usuario.getEmail())
                    .password(usuario.getSenha())
                    .roles(usuario.getTipo().name()) // Exemplo: GERENTE ou VENDEDOR
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
