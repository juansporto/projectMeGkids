package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.dto.VendaDTO; // Importar o DTO
import com.projeto.sistemameg2.dto.VendaMensalDTO;
// import com.projeto.sistemameg2.modelos.Venda; // Não precisa importar a entidade Venda aqui se você só trabalha com DTOs
import com.projeto.sistemameg2.servicos.VendaServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Define que este é um controlador REST (retorna JSON/XML por padrão)
@RequestMapping("/api/vendas") // Prefixo para TODAS as URLs desta API
public class VendaControle {

    @Autowired
    private VendaServico vendaServico;

    // Listar todas as vendas (GET /api/vendas)
    @GetMapping
    public List<VendaDTO> listar() { 
        return vendaServico.listarTodos(); // Retorna lista de VendaDTO
    }

    // Buscar venda por ID (GET /api/vendas/{id})
    @GetMapping("/{id}")
    public ResponseEntity<VendaDTO> buscarPorId(@PathVariable Long id) { 
        return vendaServico.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Obter dados para o gráfico diário (GET /api/vendas/grafico-diario)
    @GetMapping("/grafico-diario")
    public List<VendaMensalDTO> obterDadosGraficoDiario() {
        return vendaServico.obterTotaisPorDia();
    }

    // Salvar uma nova venda (POST /api/vendas)
    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody VendaDTO vendaDTO) { // Recebe VendaDTO
        try {
            VendaDTO vendaSalva = vendaServico.salvar(vendaDTO);
            return new ResponseEntity<>(vendaSalva, HttpStatus.CREATED); // Retorna 201 Created
        } catch (RuntimeException e) {
            // Retorna 400 Bad Request com a mensagem de erro
            return ResponseEntity.badRequest().body(e.getMessage()); 
        }
    }

    // Atualizar uma venda existente (PUT /api/vendas/{id})
    // NOTE: Atualizações de venda são complexas e podem requerer lógica específica no serviço.
    // O retorno e tratamento de exceções precisam estar de acordo com seu VendaServico.
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody VendaDTO vendaDTO) {
        try {
            return vendaServico.atualizar(id, vendaDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (UnsupportedOperationException e) {
            // Exemplo de retorno para operação não permitida ou não implementada
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(e.getMessage()); 
        } catch (RuntimeException e) {
            // Captura outras RuntimeExceptions do serviço
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Deletar uma venda (DELETE /api/vendas/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            if (vendaServico.deletar(id)) { // Assumindo que vendaServico.deletar(id) retorna boolean
                return ResponseEntity.noContent().build(); // 204 No Content
            }
            return ResponseEntity.notFound().build(); // 404 Not Found se não existir
        } catch (RuntimeException e) {
            // Captura qualquer exceção do serviço (ex: estoque negativo na reversão)
            // Retorna 500 Internal Server Error e o corpo da mensagem de erro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // O MÉTODO ABAIXO ESTÁ DUPLICADO E CAUSA CONFLITO. REMOVA-O.
    /*
    @PostMapping("/vendas") 
    public ResponseEntity<Venda> criarVenda(@RequestBody VendaDTO vendaDTO) { 
        Venda novaVenda = vendaServico.criarVenda(vendaDTO); // Provavelmente criarVenda deveria ser salvar
        return ResponseEntity.status(HttpStatus.CREATED).body(novaVenda);
    }
    */
}