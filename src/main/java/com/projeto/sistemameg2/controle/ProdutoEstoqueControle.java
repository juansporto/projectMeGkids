package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.dto.ProdutoEstoqueDTO;
import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.servicos.ProdutoServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // Importar Controller
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; // Importar RequestMapping

import java.util.List;
import java.util.stream.Collectors;

@Controller // <--- ADICIONE ESTA ANOTAÇÃO
@RequestMapping("/admin/estoque") // <--- ADICIONE ESTA ANOTAÇÃO (Esta define a URL base)
public class ProdutoEstoqueControle {

    @Autowired
    private ProdutoServico produtoServico;

    @GetMapping // Já estava aqui, está correto para o GET na URL base
    public String listarEstoqueSimplificado(Model model) {
        // Busca todos os produtos e os converte para o DTO simplificado
        List<ProdutoEstoqueDTO> produtosEstoque = produtoServico.listarTodos().stream()
                .map(produto -> new ProdutoEstoqueDTO(
                        produto.getId(),
                        produto.getNome(),
                        produto.getQuantidadeEstoque()
                ))
                .collect(Collectors.toList());

        model.addAttribute("produtosEstoque", produtosEstoque);
        return "admin/estoque"; // <--- Mudei para "admin/estoque" para corresponder ao erro.
        // Se seu arquivo HTML real é "estoque_simplificado.html",
        // mude esta linha para: return "admin/estoque_simplificado";
    }
}