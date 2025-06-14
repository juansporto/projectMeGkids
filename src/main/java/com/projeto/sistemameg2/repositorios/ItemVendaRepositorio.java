package com.projeto.sistemameg2.repositorios;

// ItemVendaRepository.java


import com.projeto.sistemameg2.modelos.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemVendaRepositorio extends JpaRepository<ItemVenda, Long> {
}
