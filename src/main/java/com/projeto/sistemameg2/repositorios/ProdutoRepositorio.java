package com.projeto.sistemameg2.repositorios;

import com.projeto.sistemameg2.modelos.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepositorio extends JpaRepository<Produto, Long> {
}