package com.projeto.sistemameg2.repositorios;


import org.springframework.data.jpa.repository.JpaRepository;
import com.projeto.sistemameg2.modelos.Produto;

public interface ProdutoRepositorio extends JpaRepository<Produto, Long> {
}
