package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.EstoqueMov;
import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.repositorios.EstoqueMovRepositorio;
import com.projeto.sistemameg2.repositorios.ProdutoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class EstoqueMovControle {

    @Autowired
    private EstoqueMovRepositorio estoqueMovRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @GetMapping("/listarMovimentacoes")
    public String listarMovimentacoes(Model model) {
        List<EstoqueMov> lista = estoqueMovRepositorio.findAll();
        model.addAttribute("movimentacoes", lista);
        return "administrativo/estoque/movimentacoes-lista"; // crie essa view
    }

    @GetMapping("/cadastroMovimentacao")
    public String cadastroMovimentacao(Model model) {
        model.addAttribute("estoqueMov", new EstoqueMov());
        model.addAttribute("produtos", produtoRepositorio.findAll());
        return "administrativo/estoque/movimentacoes-cadastro"; // crie essa view
    }

    @PostMapping("/salvarMovimentacao")
    public String salvarMovimentacao(@ModelAttribute EstoqueMov estoqueMov) {
        estoqueMovRepositorio.save(estoqueMov);
        return "redirect:/listarMovimentacoes";
    }

    @GetMapping("/editarMovimentacao/{id}")
    public String editarMovimentacao(@PathVariable("id") Long id, Model model) {
        Optional<EstoqueMov> mov = estoqueMovRepositorio.findById(id);
        if (mov.isPresent()) {
            model.addAttribute("estoqueMov", mov.get());
            model.addAttribute("produtos", produtoRepositorio.findAll());
            return "administrativo/estoque/movimentacoes-cadastro"; // mesma view do cadastro
        } else {
            return "redirect:/listarMovimentacoes";
        }
    }

    @GetMapping("/removerMovimentacao/{id}")
    public String removerMovimentacao(@PathVariable("id") Long id) {
        estoqueMovRepositorio.deleteById(id);
        return "redirect:/listarMovimentacoes";
    }
}
