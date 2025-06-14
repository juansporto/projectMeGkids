package com.projeto.sistemameg2.repositorios;

// VendaRepository.java


import com.projeto.sistemameg2.modelos.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendaRepositorio extends JpaRepository<Venda, Long> {
}
