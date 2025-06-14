package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Estoque;
import com.projeto.sistemameg2.servicos.EstoqueServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoques")
public class EstoqueControle {

    @Autowired
    private EstoqueServico estoqueServico;

    @GetMapping
    public List<Estoque> listar() {
        return estoqueServico.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estoque> buscarPorId(@PathVariable Long id) {
        return estoqueServico.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Estoque salvar(@RequestBody Estoque estoque) {
        return estoqueServico.salvar(estoque);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estoque> atualizar(@PathVariable Long id, @RequestBody Estoque estoque) {
        return estoqueServico.atualizar(id, estoque)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (estoqueServico.deletar(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
