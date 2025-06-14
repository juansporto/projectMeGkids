package com.projeto.sistemameg2.repositorios;



import org.springframework.data.jpa.repository.JpaRepository;
import com.projeto.sistemameg2.modelos.Cliente;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
}

