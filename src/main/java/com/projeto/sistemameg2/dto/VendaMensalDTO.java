// VendaMensalDTO.java
package com.projeto.sistemameg2.dto;

import java.math.BigDecimal;

public class VendaMensalDTO {
    private String mes; // Ex: "Jun/2025"
    private BigDecimal total;

    public VendaMensalDTO(String mes, BigDecimal total) {
        this.mes = mes;
        this.total = total;
    }

    public String getMes() { return mes; }
    public BigDecimal getTotal() { return total; }
}
