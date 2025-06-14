package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.ItemVenda;
import com.projeto.sistemameg2.servicos.ItemVendaServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itensvenda")
public class ItemVendaControle {

    @Autowired
    private ItemVendaServico itemVendaServico;

    @GetMapping
    public List<ItemVenda> listar() {
        return itemVendaServico.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemVenda> buscarPorId(@PathVariable Long id) {
        return itemVendaServico.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ItemVenda salvar(@RequestBody ItemVenda itemVenda) {
        return itemVendaServico.salvar(itemVenda);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemVenda> atualizar(@PathVariable Long id, @RequestBody ItemVenda itemVenda) {
        return itemVendaServico.atualizar(id, itemVenda)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (itemVendaServico.deletar(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

