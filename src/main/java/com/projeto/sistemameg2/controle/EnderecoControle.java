package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Endereco;
import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.repositorios.EnderecoRepositorio;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EnderecoControle {

    @Autowired
    private EnderecoRepositorio enderecoRepositorio;

    @GetMapping("/cadastroEndereco")
    public String cadastroEndereco(Model model) {
        model.addAttribute("endereco", new Endereco());
        return "administrativo/enderecos/cadastro";
    }

    @GetMapping("/listarEndereco")
    public String listarEndereco(Model model) {
        model.addAttribute("enderecos", enderecoRepositorio.findAll());
        return "administrativo/enderecos/lista";
    }

    @PostMapping("/salvarEndereco")
    public String salvarEndereco(@ModelAttribute Endereco endereco) {
        enderecoRepositorio.save(endereco);
        return "redirect:/listarEndereco";
    }

    @GetMapping("/editarEndereco/{id}")
    public String editarEndereco(@PathVariable("id") Long id, Model model) {
        Endereco endereco = enderecoRepositorio.findById(id).orElse(null);
        model.addAttribute("endereco", endereco);
        return "administrativo/enderecos/cadastro";
    }

    @GetMapping("/removerEndereco/{id}")
    public String removerEndereco(@PathVariable("id") Long id) {
        enderecoRepositorio.deleteById(id);
        return "redirect:/listarEndereco";
    }
}


