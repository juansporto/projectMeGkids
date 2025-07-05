package com.projeto.sistemameg2.dto;

import com.projeto.sistemameg2.modelos.Relatorio;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RelatorioDTO {

    private Long id;
    private String nomeRelatorio;
    private String tipoRelatorio; // String para o enum
    private String dataGeracao;   // String formatada
    private String periodoInicial; // String formatada
    private String periodoFinal;   // String formatada
    private String parametrosFiltros; // JSON como String
    private String dadosSumarizados;  // JSON como String
    private Long geradoPorUsuarioId;
    private String status;        // String para o enum
    private String descricao;

    public RelatorioDTO() {}

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

    public String getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(String tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public String getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(String dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public String getPeriodoInicial() {
        return periodoInicial;
    }

    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    public String getPeriodoFinal() {
        return periodoFinal;
    }

    public void setPeriodoFinal(String periodoFinal) {
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

    public Long getGeradoPorUsuarioId() {
        return geradoPorUsuarioId;
    }

    public void setGeradoPorUsuarioId(Long geradoPorUsuarioId) {
        this.geradoPorUsuarioId = geradoPorUsuarioId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // Método de conversão de Entidade para DTO
    public static RelatorioDTO fromEntity(Relatorio relatorio) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setId(relatorio.getId());
        dto.setNomeRelatorio(relatorio.getNomeRelatorio());
        dto.setTipoRelatorio(relatorio.getTipoRelatorio().name()); // Converte Enum para String
        dto.setDataGeracao(formatLocalDateTime(relatorio.getDataGeracao()));
        dto.setPeriodoInicial(formatLocalDateTime(relatorio.getPeriodoInicial()));
        dto.setPeriodoFinal(formatLocalDateTime(relatorio.getPeriodoFinal()));
        dto.setParametrosFiltros(relatorio.getParametrosFiltros());
        dto.setDadosSumarizados(relatorio.getDadosSumarizados());
        if (relatorio.getGeradoPorUsuario() != null) {
            dto.setGeradoPorUsuarioId(relatorio.getGeradoPorUsuario().getId());
        }
        dto.setStatus(relatorio.getStatus().name()); // Converte Enum para String
        dto.setDescricao(relatorio.getDescricao());
        return dto;
    }

    // Helper para formatar LocalDateTime para String
    private static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }
}
