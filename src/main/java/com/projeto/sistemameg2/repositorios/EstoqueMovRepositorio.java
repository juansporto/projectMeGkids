package com.projeto.sistemameg2.repositorios;

import com.projeto.sistemameg2.modelos.EstoqueMov;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstoqueMovRepositorio extends JpaRepository<EstoqueMov, Long> {
    List<EstoqueMov> findByProdutoId(Long produtoId);
}
