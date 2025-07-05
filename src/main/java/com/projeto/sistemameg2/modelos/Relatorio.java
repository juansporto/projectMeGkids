package com.projeto.sistemameg2.modelos;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "relatorios")
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nomeRelatorio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoRelatorio tipoRelatorio;

    @Column(nullable = false)
    private LocalDateTime dataGeracao;

    @Column(nullable = true)
    private LocalDateTime periodoInicial;

    @Column(nullable = true)
    private LocalDateTime periodoFinal;

    @Column(columnDefinition = "TEXT")
    private String parametrosFiltros; // Armazenará JSON como String

    @Column(columnDefinition = "TEXT")
    private String dadosSumarizados; // Armazenará o JSON com os dados do relatório como String

    @ManyToOne(optional = true)
    @JoinColumn(name = "usuario_id")
    private Usuario geradoPorUsuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusRelatorio status;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    public enum TipoRelatorio {
        VENDAS_POR_PERIODO,
        ESTOQUE_ATUAL,
        MOVIMENTACOES_DETALHADAS,
        PRODUTOS_MAIS_VENDIDOS,
        // Adicione outros tipos conforme necessário
    }

    public enum StatusRelatorio {
        GERADO,
        PROCESSANDO,
        ERRO,
        AGENDADO
    }

    @PrePersist
    public void prePersist() {
        this.dataGeracao = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusRelatorio.GERADO;
        }
    }

    // --- Getters e Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeRelatorio() {
        return nomeRelatorio;
    }

    public void setNomeRelatorio(String nomeRelatorio) {
        this.nomeRelatorio = nomeRelatorio;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public LocalDateTime getPeriodoInicial() {
        return periodoInicial;
    }

    public void setPeriodoInicial(LocalDateTime periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    public LocalDateTime getPeriodoFinal() {
        return periodoFinal;
    }

    public void setPeriodoFinal(LocalDateTime periodoFinal) {
        this.periodoFinal = periodoFinal;
    }

    public String getParametrosFiltros() {
        return parametrosFiltros;
    }

    public void setParametrosFiltros(String parametrosFiltros) {
        this.parametrosFiltros = parametrosFiltros;
    }

    public String getDadosSumarizados() {
        return dadosSumarizados;
    }

    public void setDadosSumarizados(String dadosSumarizados) {
        this.dadosSumarizados = dadosSumarizados;
    }

    public Usuario getGeradoPorUsuario() {
        return geradoPorUsuario;
    }

    public void setGeradoPorUsuario(Usuario geradoPorUsuario) {
        this.geradoPorUsuario = geradoPorUsuario;
    }

    public StatusRelatorio getStatus() {
        return status;
    }

    public void setStatus(StatusRelatorio status) {
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relatorio)) return false;
        Relatorio relatorio = (Relatorio) o;
        return id != null && id.equals(relatorio.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
