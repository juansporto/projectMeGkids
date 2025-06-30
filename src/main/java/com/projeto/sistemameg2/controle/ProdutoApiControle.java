// src/main/java/com/projeto/sistemameg2/controle/ProdutoAPIControle.java
package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.servicos.ProdutoServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; // Usar @RestController para APIs REST

import java.util.List;
import java.util.Optional;

@RestController // Indica que este controlador retorna dados (JSON/XML), não views
@RequestMapping("/api/produtos") // URL base para as APIs de produto
public class ProdutoApiControle {

    @Autowired
    private ProdutoServico produtoServico;

    // Endpoint para buscar um produto pelo código de barras (GET /api/produtos/por-codigo/{codigoBarras})
    @GetMapping("/por-codigo/{codigoBarras}")
    public ResponseEntity<Produto> getProdutoByCodigoBarras(@PathVariable String codigoBarras) {
        Optional<Produto> produto = produtoServico.buscarPorCodigoBarras(codigoBarras);
        return produto.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // Opcional: Um endpoint para listar todos os produtos via API (GET /api/produtos)
    // Se seu frontend de vendas usa PRODUTOS_API_URL para carregar todos os produtos no dropdown
    @GetMapping
    public List<Produto> listarTodosProdutosApi() {
        return produtoServico.listarTodos();
    }

    // Opcional: Um endpoint para buscar produto por ID via API (GET /api/produtos/{id})
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorIdApi(@PathVariable Long id) {
        return produtoServico.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}