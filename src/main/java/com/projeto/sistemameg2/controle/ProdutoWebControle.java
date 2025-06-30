// src/main/java/com/projeto/sistemameg2/controle/ProdutoWebControle.java
package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.modelos.Categoria; // Importe a categoria
import com.projeto.sistemameg2.servicos.ProdutoServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/produtos") // URL para acesso via navegador (admin para gerente)
public class ProdutoWebControle {

    @Autowired
    private ProdutoServico produtoServico;

    // Exibe a lista de produtos
    @GetMapping
    public String listarProdutos(Model model) {
        List<Produto> produtos = produtoServico.listarTodos();
        model.addAttribute("produtos", produtos);
        return "admin/produtoslista"; // Retorna o nome do template Thymeleaf (localizado em src/main/resources/templates/admin/produtoslista.html)
    }

    // Exibe o formulário para um novo produto
    @GetMapping("/novo")
    public String exibirFormularioDeNovoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("categorias", produtoServico.listarTodasCategorias()); // Para preencher o dropdown de categorias
        return "admin/produtosform"; // Retorna o nome do template Thymeleaf (localizado em src/main/resources/templates/admin/produtosform.html)
    }

    // Exibe o formulário para editar um produto existente
    @GetMapping("/editar/{id}")
    public String exibirFormularioDeEdicaoDeProduto(@PathVariable Long id, Model model, RedirectAttributes attributes) {
        Optional<Produto> produtoOptional = produtoServico.buscarPorId(id);
        if (produtoOptional.isPresent()) {
            model.addAttribute("produto", produtoOptional.get());
            model.addAttribute("categorias", produtoServico.listarTodasCategorias()); // Para preencher o dropdown de categorias
            return "admin/produtosform";
        } else {
            attributes.addFlashAttribute("mensagemErro", "Produto não encontrado!");
            return "redirect:/admin/produtos"; // Redireciona para a lista se o produto não for encontrado
        }
    }

    // Salva ou atualiza um produto
    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute("produto") Produto produto, RedirectAttributes attributes) {
        // Log para depuração: verificar o ID da categoria recebido
        if (produto.getCategoria() != null) {
            System.out.println("ID da Categoria recebido no formulário: " + produto.getCategoria().getId());
        }

        produtoServico.salvar(produto);
        attributes.addFlashAttribute("mensagemSucesso", "Produto salvo com sucesso!");
        return "redirect:/admin/produtos";
    }

    // Deleta um produto
    @GetMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable Long id, RedirectAttributes attributes) {
        if (produtoServico.deletar(id)) {
            attributes.addFlashAttribute("mensagemSucesso", "Produto excluído com sucesso!");
        } else {
            attributes.addFlashAttribute("mensagemErro", "Erro ao excluir produto. Produto não encontrado!");
        }
        return "redirect:/admin/produtos";
    }
}