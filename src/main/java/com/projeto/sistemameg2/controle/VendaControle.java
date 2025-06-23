package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.dto.VendaDTO; // Importar o DTO
import com.projeto.sistemameg2.servicos.VendaServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendas") // URL da API REST
public class VendaControle {

    @Autowired
    private VendaServico vendaServico;

    // Listar todas as vendas
    @GetMapping
    public List<VendaDTO> listar() { // Retorna lista de VendaDTO
        return vendaServico.listarTodos();
    }

    // Buscar venda por ID
    @GetMapping("/{id}")
    public ResponseEntity<VendaDTO> buscarPorId(@PathVariable Long id) { // Retorna VendaDTO
        return vendaServico.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Salvar uma nova venda
    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody VendaDTO vendaDTO) { // Recebe VendaDTO
        try {
            VendaDTO vendaSalva = vendaServico.salvar(vendaDTO);
            return new ResponseEntity<>(vendaSalva, HttpStatus.CREATED); // Retorna 201 Created
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Retorna 400 Bad Request
        }
    }

    // Atualizar uma venda existente
    // NOTE: Atualizações de venda são complexas, este método apenas chama o serviço
    // que agora lançará uma exceção ou requer lógica complexa.
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody VendaDTO vendaDTO) {
        try {
            return vendaServico.atualizar(id, vendaDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(e.getMessage()); // 405 Method Not Allowed
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Deletar uma venda
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            if (vendaServico.deletar(id)) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }
            return ResponseEntity.notFound().build(); // 404 Not Found se não existir
        } catch (RuntimeException e) {
            // Se o deletar lançar uma exceção (ex: estoque negativo na reversão)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}