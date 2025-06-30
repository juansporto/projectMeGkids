package com.projeto.sistemameg2.modelos; // Ou o pacote correto da sua entidade

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList; // Para inicializar a lista

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false) // Assumindo que a venda é feita por um usuário (vendedor)
    private Usuario usuario;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemVenda> itensVenda = new ArrayList<>(); // Inicialize a lista

    // Atributos da Venda
    @Column(nullable = false)
    private LocalDateTime dataVenda;

    @Column(nullable = false, precision = 10, scale = 2) // Exemplo de precisão para BigDecimal
    private BigDecimal valorTotal;

    @Column(nullable = false)
    private String formaPagamento;

    @Column(precision = 10, scale = 2, nullable = true) // NOVO: Valor recebido (pode ser nulo se não for dinheiro)
    private BigDecimal valorRecebido;

    // Construtor padrão
    public Venda() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<ItemVenda> getItensVenda() { return itensVenda; }
    public void setItensVenda(List<ItemVenda> itensVenda) { this.itensVenda = itensVenda; }

    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public BigDecimal getValorRecebido() { return valorRecebido; }
    public void setValorRecebido(BigDecimal valorRecebido) { this.valorRecebido = valorRecebido; }

    // Métodos utilitários para adicionar/remover itens de forma bidirecional
    public void addItemVenda(ItemVenda item) {
        itensVenda.add(item);
        item.setVenda(this);
    }

    public void removeItemVenda(ItemVenda item) {
        itensVenda.remove(item);
        item.setVenda(null);
    }
}