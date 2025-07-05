// Exemplo:
package com.projeto.sistemameg2.repositorios;

import com.projeto.sistemameg2.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email); // <-- Adicione este método
}