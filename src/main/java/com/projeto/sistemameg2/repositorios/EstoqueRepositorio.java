package com.projeto.sistemameg2.repositorios;

// EstoqueRepository.java


import com.projeto.sistemameg2.modelos.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstoqueRepositorio extends JpaRepository<Estoque, Long> {
}
