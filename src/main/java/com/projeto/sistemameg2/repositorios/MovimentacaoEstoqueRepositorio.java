package com.projeto.sistemameg2.repositorios;

// MovimentacaoEstoqueRepository.java


import com.projeto.sistemameg2.modelos.MovimentacaoEstoque;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoEstoqueRepositorio extends JpaRepository<MovimentacaoEstoque, Long> {
    boolean existsByUsuarioId(Long usuarioId);
    
    List<MovimentacaoEstoque> findByDataBetween(LocalDateTime inicio, LocalDateTime fim);


}

