package com.projeto.sistemameg2.dto;

import java.math.BigDecimal;
import java.util.List;

public class VendaDTO {
    private Long id;
    private Long clienteId;
    private Long usuarioId; // ID do usuário que está registrando a venda
    private String formaPagamento;
    // O valorTotal será calculado no backend, não precisa vir do frontend
    // A dataVenda também pode ser gerada no backend

    private List<ItemVendaDTO> itens; // Lista de DTOs dos itens

    public VendaDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
    public List<ItemVendaDTO> getItens() { return itens; }
    public void setItens(List<ItemVendaDTO> itens) { this.itens = itens; }

    // Adicione getters/setters para valorTotal e dataVenda se for usá-los para *retorno* da API
    private BigDecimal valorTotal;
    private String dataVenda;

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public String getDataVenda() { return dataVenda; }
    public void setDataVenda(String dataVenda) { this.dataVenda = dataVenda; }
}