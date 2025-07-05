package com.projeto.sistemameg2.servicos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.sistemameg2.modelos.MovimentacaoEstoque;
import com.projeto.sistemameg2.modelos.Relatorio;
import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.repositorios.MovimentacaoEstoqueRepositorio;
import com.projeto.sistemameg2.repositorios.RelatorioRepositorio;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// Importações para iText
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.BaseColor;


@Service
public class RelatorioServico {

    @Autowired
    private RelatorioRepositorio relatorioRepositorio;

    @Autowired
    private MovimentacaoEstoqueRepositorio movimentacaoEstoqueRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Método de Geração de Relatório de Movimentações por Período (já existente)
    @Transactional
    public Relatorio gerarRelatorioMovimentacoesPorPeriodo(
            LocalDateTime inicio, LocalDateTime fim, Long usuarioId) throws Exception {

        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        List<MovimentacaoEstoque> movimentacoes = movimentacaoEstoqueRepositorio
                .findByDataBetween(inicio, fim);

        Map<String, Object> dadosAgregados = new HashMap<>();
        int totalEntradas = 0;
        int totalSaidas = 0;
        Map<String, Integer> quantidadePorProduto = new HashMap<>();

        for (MovimentacaoEstoque mov : movimentacoes) {
            if (mov.getTipo() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA) {
                totalEntradas += mov.getQuantidade();
            } else {
                totalSaidas += mov.getQuantidade();
            }
            String nomeProduto = mov.getProduto().getNome();
            quantidadePorProduto.put(nomeProduto, quantidadePorProduto.getOrDefault(nomeProduto, 0) + mov.getQuantidade());
        }

        dadosAgregados.put("totalEntradas", totalEntradas);
        dadosAgregados.put("totalSaidas", totalSaidas);
        dadosAgregados.put("detalhePorProduto", quantidadePorProduto);
        dadosAgregados.put("movimentacoes", movimentacoes.stream()
            .map(mov -> {
                Map<String, Object> movMap = new HashMap<>();
                movMap.put("id", mov.getId());
                movMap.put("produto", mov.getProduto().getNome());
                movMap.put("tipo", mov.getTipo().name());
                movMap.put("quantidade", mov.getQuantidade());
                movMap.put("data", mov.getData().toString());
                movMap.put("usuario", mov.getUsuario().getNome()); // Supondo que Usuario tenha um campo 'nome'
                return movMap;
            }).collect(Collectors.toList()));


        Relatorio relatorio = new Relatorio();
        relatorio.setNomeRelatorio("Movimentações de Estoque de " + inicio.toLocalDate() + " a " + fim.toLocalDate());
        relatorio.setTipoRelatorio(Relatorio.TipoRelatorio.MOVIMENTACOES_DETALHADAS);
        relatorio.setPeriodoInicial(inicio);
        relatorio.setPeriodoFinal(fim);
        relatorio.setGeradoPorUsuario(usuario);
        relatorio.setStatus(Relatorio.StatusRelatorio.GERADO);
        relatorio.setDescricao("Relatório detalhado de todas as movimentações de estoque no período especificado.");

        relatorio.setDadosSumarizados(objectMapper.writeValueAsString(dadosAgregados));
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("inicio", inicio.toString());
        parametros.put("fim", fim.toString());
        relatorio.setParametrosFiltros(objectMapper.writeValueAsString(parametros));

        return relatorioRepositorio.save(relatorio);
    }

    /**
     * Gera um PDF para um relatório específico.
     * @param relatorioId O ID do relatório a ser gerado em PDF.
     * @return Um array de bytes contendo o conteúdo do PDF.
     * @throws RuntimeException se o relatório não for encontrado ou houver erro na geração.
     */
    public byte[] gerarPdfRelatorio(Long relatorioId) throws Exception {
        Optional<Relatorio> optRelatorio = relatorioRepositorio.findById(relatorioId);
        if (optRelatorio.isEmpty()) {
            throw new RuntimeException("Relatório não encontrado com o ID: " + relatorioId);
        }

        Relatorio relatorio = optRelatorio.get();
        Map<String, Object> dados = objectMapper.readValue(relatorio.getDadosSumarizados(), Map.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Título do Relatório
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
            Paragraph titulo = new Paragraph(relatorio.getNomeRelatorio(), fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10);
            document.add(titulo);

            // Informações Gerais
            Font fontSubtitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
            Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

            document.add(new Paragraph("Tipo: " + relatorio.getTipoRelatorio().name(), fontNormal));
            document.add(new Paragraph("Data de Geração: " + relatorio.getDataGeracao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), fontNormal));
            if (relatorio.getPeriodoInicial() != null && relatorio.getPeriodoFinal() != null) {
                document.add(new Paragraph("Período: " + relatorio.getPeriodoInicial().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a " + relatorio.getPeriodoFinal().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontNormal));
            }
            if (relatorio.getGeradoPorUsuario() != null) {
                document.add(new Paragraph("Gerado por: " + relatorio.getGeradoPorUsuario().getNome(), fontNormal)); // Supondo que Usuario tenha um campo 'nome'
            }
            document.add(new Paragraph("\n")); // Espaço

            // Resumo dos dados (Exemplo para MOVIMENTACOES_DETALHADAS)
            if (relatorio.getTipoRelatorio() == Relatorio.TipoRelatorio.MOVIMENTACOES_DETALHADAS) {
                document.add(new Paragraph("Resumo das Movimentações:", fontSubtitulo));
                document.add(new Paragraph("Total de Entradas: " + dados.get("totalEntradas"), fontNormal));
                document.add(new Paragraph("Total de Saídas: " + dados.get("totalSaidas"), fontNormal));
                document.add(new Paragraph("\n"));

                // Tabela de Detalhes das Movimentações
                document.add(new Paragraph("Detalhes das Movimentações:", fontSubtitulo));
                document.add(new Paragraph("\n"));

                List<Map<String, Object>> movimentacoes = (List<Map<String, Object>>) dados.get("movimentacoes");
                if (movimentacoes != null && !movimentacoes.isEmpty()) {
                    PdfPTable table = new PdfPTable(5); // 5 colunas: ID, Produto, Tipo, Quantidade, Data
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10f);
                    table.setSpacingAfter(10f);

                    // Cabeçalho da tabela
                    Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
                    PdfPCell cell;

                    cell = new PdfPCell(new Phrase("ID", fontHeader));
                    cell.setBackgroundColor(BaseColor.DARK_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("Produto", fontHeader));
                    cell.setBackgroundColor(BaseColor.DARK_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("Tipo", fontHeader));
                    cell.setBackgroundColor(BaseColor.DARK_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("Quantidade", fontHeader));
                    cell.setBackgroundColor(BaseColor.DARK_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("Data", fontHeader));
                    cell.setBackgroundColor(BaseColor.DARK_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    table.addCell(cell);

                    // Linhas da tabela
                    for (Map<String, Object> mov : movimentacoes) {
                        table.addCell(new Phrase(String.valueOf(mov.get("id")), fontNormal));
                        table.addCell(new Phrase(String.valueOf(mov.get("produto")), fontNormal));
                        table.addCell(new Phrase(String.valueOf(mov.get("tipo")), fontNormal));
                        table.addCell(new Phrase(String.valueOf(mov.get("quantidade")), fontNormal));
                        table.addCell(new Phrase(String.valueOf(mov.get("data")).substring(0, 16), fontNormal)); // Formata data
                    }
                    document.add(table);
                } else {
                    document.add(new Paragraph("Nenhuma movimentação encontrada para o período.", fontNormal));
                }
            }
            // Adicione lógica para outros tipos de relatório aqui
            // else if (relatorio.getTipoRelatorio() == Relatorio.TipoRelatorio.ESTOQUE_ATUAL) { ... }

        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar o documento PDF: " + e.getMessage(), e);
        } finally {
            document.close();
        }

        return baos.toByteArray();
    }
}
