package com.projeto.sistemameg2.repositorios;

import com.projeto.sistemameg2.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
}
