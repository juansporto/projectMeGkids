package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.repositorios.ProdutoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProdutoControle {

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @GetMapping("/cadastroProduto")
    public String cadastroProduto(Model model) {
        model.addAttribute("produto", new Produto());
        return "administrativo/produtos/cadastro";
    }

    @GetMapping("/listarProduto")
    public String listarProduto(Model model) {
        model.addAttribute("produtos", produtoRepositorio.findAll());
        return "administrativo/produtos/lista";
    }

    @PostMapping("/salvarProduto")
    public String salvarProduto(@ModelAttribute Produto produto) {
        produtoRepositorio.save(produto);
        return "redirect:/listarProduto";
    }

    @GetMapping("/editarProduto/{id}")
    public String editarProduto(@PathVariable("id") Long id, Model model) {
        Produto produto = produtoRepositorio.findById(id).orElse(null);
        model.addAttribute("produto", produto);
        return "administrativo/produtos/cadastro";
    }

    @GetMapping("/removerProduto/{id}")
    public String removerProduto(@PathVariable("id") Long id) {
        produtoRepositorio.deleteById(id);
        return "redirect:/listarProduto";
    }
}
