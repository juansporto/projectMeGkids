package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.dto.MovimentacaoEstoqueDTO;
import com.projeto.sistemameg2.servicos.MovimentacaoEstoqueServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimentacoes")
public class MovimentacaoEstoqueControle {

    @Autowired
    private MovimentacaoEstoqueServico movimentacaoEstoqueServico;

    @GetMapping
    public List<MovimentacaoEstoqueDTO> listar() {
        return movimentacaoEstoqueServico.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoEstoqueDTO> buscarPorId(@PathVariable Long id) {
        return movimentacaoEstoqueServico.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody MovimentacaoEstoqueDTO dto) {
        try {
            MovimentacaoEstoqueDTO salvo = movimentacaoEstoqueServico.salvar(dto);
            return ResponseEntity.ok(salvo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody MovimentacaoEstoqueDTO dto) {
        try {
            return movimentacaoEstoqueServico.atualizar(id, dto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (movimentacaoEstoqueServico.deletar(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
