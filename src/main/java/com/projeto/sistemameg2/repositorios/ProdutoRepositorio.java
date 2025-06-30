// src/main/java/com/projeto/sistemameg2/repositorios/ProdutoRepositorio.java
package com.projeto.sistemameg2.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.projeto.sistemameg2.modelos.Produto;

public interface ProdutoRepositorio extends JpaRepository<Produto, Long> {
    Optional<Produto> findByCodigoBarras(String codigoBarras);
}