package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.dto.RelatorioDTO;
import com.projeto.sistemameg2.servicos.RelatorioServico;
import com.projeto.sistemameg2.repositorios.RelatorioRepositorio;
import com.projeto.sistemameg2.modelos.Relatorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/relatorios")
public class RelatorioControle {

    @Autowired
    private RelatorioRepositorio relatorioRepositorio;

    @Autowired
    private RelatorioServico relatorioServico;

    @GetMapping
    public List<RelatorioDTO> listarTodos() {
        return relatorioRepositorio.findAll().stream()
                .map(RelatorioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelatorioDTO> buscarPorId(@PathVariable Long id) {
        return relatorioRepositorio.findById(id)
                .map(RelatorioDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (relatorioRepositorio.existsById(id)) {
            relatorioRepositorio.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/gerar/movimentacoes-por-periodo")
    public ResponseEntity<?> gerarMovimentacoesPorPeriodo(@RequestBody Map<String, String> request) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(request.get("inicio"));
            LocalDateTime fim = LocalDateTime.parse(request.get("fim"));
            Long usuarioId = Long.parseLong(request.get("usuarioId"));

            Relatorio relatorioGerado = relatorioServico.gerarRelatorioMovimentacoesPorPeriodo(inicio, fim, usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(RelatorioDTO.fromEntity(relatorioGerado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    /**
     * Endpoint para baixar o PDF de um relatório específico.
     * @param id O ID do relatório.
     * @return O arquivo PDF como um array de bytes.
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> baixarPdf(@PathVariable Long id) {
        try {
            byte[] pdfBytes = relatorioServico.gerarPdfRelatorio(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "relatorio_" + id + ".pdf";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Captura a exceção de "Relatório não encontrado" ou outros erros de serviço
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage().getBytes());
        } catch (Exception e) {
            // Captura outras exceções inesperadas durante a geração do PDF
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(("Erro interno ao gerar PDF: " + e.getMessage()).getBytes());
        }
    }
}
