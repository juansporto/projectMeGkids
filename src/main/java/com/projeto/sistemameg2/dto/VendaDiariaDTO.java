// package com.projeto.sistemameg2.dto;
// VendaDiariaDTO.java

package com.projeto.sistemameg2.dto;

import java.math.BigDecimal;

public class VendaDiariaDTO {
    private String dia; // Formato: "dd/MM" ou "dd/MM/yyyy"
    private BigDecimal total;

    public VendaDiariaDTO(String dia, BigDecimal total) {
        this.dia = dia;
        this.total = total;
    }

    // Getters
    public String getDia() {
        return dia;
    }

    public BigDecimal getTotal() {
        return total;
    }

    // Setters (opcional, dependendo do uso)
    public void setDia(String dia) {
        this.dia = dia;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}