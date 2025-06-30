package com.projeto.sistemameg2.dto;

import java.math.BigDecimal;

public class ItemVendaDTO {
    private Long produtoId;
    private String nomeProduto; // Opcional, para exibição no frontend
    private Integer quantidade;
    private BigDecimal precoUnitario; // Opcional, o backend pode pegar do produto
    private BigDecimal subTotal; // Opcional, o backend pode calcular

    public ItemVendaDTO() {}

    // Getters e Setters
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }
}