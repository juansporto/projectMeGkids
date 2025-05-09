package com.projeto.sistemameg2.repositorios;

import com.projeto.sistemameg2.modelos.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepositorio extends JpaRepository<Endereco, Long> {
    // Aqui você pode adicionar métodos personalizados de consulta, se necessário
    // Por exemplo:
    // List<Endereco> findByClienteId(Long clienteId);
}
