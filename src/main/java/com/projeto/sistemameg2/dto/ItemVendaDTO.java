package com.projeto.sistemameg2.dto;

import java.math.BigDecimal;

public class ItemVendaDTO {
    private Long produtoId;
    private Integer quantidade;
    // Removendo precoUnitario e subTotal daqui para que sejam calculados apenas no backend
    // Você pode decidir se quer enviá-los do frontend ou não.
    // Se vierem do frontend, o backend ainda deve validá-los e, idealmente, recalcular.

    public ItemVendaDTO() {}

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}