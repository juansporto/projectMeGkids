package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.EstoqueMov;
import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.repositorios.EstoqueMovRepositorio;
import com.projeto.sistemameg2.repositorios.ProdutoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EstoqueMovControle {

    @Autowired
    private EstoqueMovRepositorio estoqueMovRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @GetMapping("/cadastroEstoqueMov")
    public String cadastroEstoqueMov(Model model) {
        model.addAttribute("estoqueMov", new EstoqueMov());
        model.addAttribute("produtos", produtoRepositorio.findAll());
        return "administrativo/estoques/cadastro";
    }

    @GetMapping("/listarEstoqueMov")
    public String listarEstoqueMov(Model model) {
        model.addAttribute("estoques", estoqueMovRepositorio.findAll());
        return "administrativo/estoques/lista";
    }

    @PostMapping("/salvarEstoqueMov")
    public String salvarEstoqueMov(@ModelAttribute EstoqueMov estoqueMov) {
        estoqueMovRepositorio.save(estoqueMov);
        return "redirect:/listarEstoqueMov";
    }

    @GetMapping("/editarEstoqueMov/{id}")
    public String editarEstoqueMov(@PathVariable("id") Long id, Model model) {
        EstoqueMov estoqueMov = estoqueMovRepositorio.findById(id).orElse(null);
        model.addAttribute("estoqueMov", estoqueMov);
        model.addAttribute("produtos", produtoRepositorio.findAll());
        return "administrativo/estoques/cadastro";
    }

    @GetMapping("/removerEstoqueMov/{id}")
    public String removerEstoqueMov(@PathVariable("id") Long id) {
        estoqueMovRepositorio.deleteById(id);
        return "redirect:/listarEstoqueMov";
    }
}
