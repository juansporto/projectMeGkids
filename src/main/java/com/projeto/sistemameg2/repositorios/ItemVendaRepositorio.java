// src/main/java/com/projeto/sistemameg2/repositorios/ItemVendaRepositorio.java
package com.projeto.sistemameg2.repositorios;

import com.projeto.sistemameg2.modelos.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemVendaRepositorio extends JpaRepository<ItemVenda, Long> {
    boolean existsByProdutoId(Long produtoId); // Método para verificar se há itens de venda para um produto
}