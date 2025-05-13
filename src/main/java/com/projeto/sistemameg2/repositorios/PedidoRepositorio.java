package com.projeto.sistemameg2.repositorios;

import com.projeto.sistemameg2.modelos.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long usuarioId);
}
