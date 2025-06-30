// src/main/java/com/projeto/sistemameg2/controle/ProdutoEstoqueControle.java
package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.dto.ProdutoEstoqueDTO;
import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.servicos.ProdutoServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/estoque") // Esta define a URL base para o controle de estoque
public class ProdutoEstoqueControle {

    @Autowired
    private ProdutoServico produtoServico;

    @GetMapping
    public String listarEstoqueSimplificado(Model model) {
        List<ProdutoEstoqueDTO> produtosEstoque = produtoServico.listarTodos().stream()
                .map(produto -> new ProdutoEstoqueDTO(
                        produto.getId(),
                        produto.getNome(),
                        produto.getQuantidadeEstoque()
                ))
                .collect(Collectors.toList());

        model.addAttribute("produtosEstoque", produtosEstoque);
        return "admin/estoque"; // Confirme que seu arquivo HTML é 'estoque.html' em 'templates/admin/'
    }
}