package com.projeto.sistemameg2.repositorios;

import com.projeto.sistemameg2.modelos.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatorioRepositorio extends JpaRepository<Relatorio, Long> {
    // Métodos de consulta personalizados podem ser adicionados aqui, se necessário.
    // Ex: List<Relatorio> findByTipoRelatorio(Relatorio.TipoRelatorio tipo);
    // Ex: List<Relatorio> findByDataGeracaoBetween(LocalDateTime start, LocalDateTime end);
}
