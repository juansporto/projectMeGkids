
package com.projeto.sistemameg2.repositorios;

import com.projeto.sistemameg2.modelos.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepositorio extends JpaRepository<Venda, Long> {

    // Query para somar o valor total das vendas agrupadas por data
    // Retorna um Object[] onde [0] é a data (LocalDateTime) e [1] é o valor total (BigDecimal)
 @Query("SELECT TO_CHAR(v.dataVenda, 'DD/MM'), SUM(v.valorTotal) " +
       "FROM Venda v " +
       "WHERE v.dataVenda >= ?1 AND v.dataVenda <= ?2 " +
       "GROUP BY TO_CHAR(v.dataVenda, 'DD/MM') " +
       "ORDER BY TO_CHAR(v.dataVenda, 'DD/MM') ASC")
List<Object[]> sumVendasByDay(LocalDateTime startOfWeek, LocalDateTime endOfWeek);

}