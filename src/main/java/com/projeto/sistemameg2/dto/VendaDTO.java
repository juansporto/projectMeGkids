package com.projeto.sistemameg2.dto;

import java.math.BigDecimal;
import java.util.List;

public class VendaDTO {
    private Long id;
    private Long clienteId;
    private Long usuarioId; // ID do usuário que está registrando a venda
    private String formaPagamento;
    private BigDecimal valorRecebido; // Valor que o cliente pagou em dinheiro

    private List<ItemVendaDTO> itens; // Lista de DTOs dos itens

    // Campos para *retorno* da API (populados pelo backend)
    private BigDecimal valorTotal;
    private String dataVenda;

    public VendaDTO() {}

    // Getters e Setters para todos os campos
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
    
    public BigDecimal getValorRecebido() { return valorRecebido; }
    public void setValorRecebido(BigDecimal valorRecebido) { this.valorRecebido = valorRecebido; }
    
    public List<ItemVendaDTO> getItens() { return itens; }
    public void setItens(List<ItemVendaDTO> itens) { this.itens = itens; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; } // <--- CORREÇÃO AQUI
    public String getDataVenda() { return dataVenda; }
    public void setDataVenda(String dataVenda) { this.dataVenda = dataVenda; }
}