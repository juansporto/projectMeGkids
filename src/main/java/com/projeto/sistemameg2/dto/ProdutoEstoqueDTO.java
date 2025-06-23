package com.projeto.sistemameg2.dto;

public class ProdutoEstoqueDTO {
    private Long id;
    private String nome;
    private Integer quantidadeEstoque;

    public ProdutoEstoqueDTO() {
    }

    public ProdutoEstoqueDTO(Long id, String nome, Integer quantidadeEstoque) {
        this.id = id;
        this.nome = nome;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }
}