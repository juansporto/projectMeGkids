package com.projeto.sistemameg2.dto;

import com.projeto.sistemameg2.modelos.MovimentacaoEstoque;

import java.time.format.DateTimeFormatter;

public class MovimentacaoEstoqueDTO {

    private Long id;
    private Long produtoId;
    private Long usuarioId;
    private MovimentacaoEstoque.TipoMovimentacao tipo;
    private int quantidade;
    private String data;

    public MovimentacaoEstoqueDTO() {}

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public MovimentacaoEstoque.TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(MovimentacaoEstoque.TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    // Conversão de Entity para DTO
    public static MovimentacaoEstoqueDTO fromEntity(MovimentacaoEstoque mov) {
        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO();
        dto.setId(mov.getId());
        dto.setProdutoId(mov.getProduto().getId());
        dto.setUsuarioId(mov.getUsuario().getId());
        dto.setTipo(mov.getTipo());
        dto.setQuantidade(mov.getQuantidade());

        // Formatação da data para "dd/MM/yyyy HH:mm"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dto.setData(mov.getData().format(formatter));

        return dto;
    }
}
