package com.projeto.sistemameg2.modelos;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_venda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venda_id", nullable = false) // Garanta que não pode ser nulo
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false) // Garanta que não pode ser nulo
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false)
    private BigDecimal precoUnitario;

    @Column(name = "sub_total", nullable = false) // <-- NOVO CAMPO
    private BigDecimal subTotal;

    public ItemVenda() {}

    // Getters e Setters (adicionar para subTotal)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubTotal() { // <-- NOVO GETTER
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) { // <-- NOVO SETTER
        this.subTotal = subTotal;
    }
}