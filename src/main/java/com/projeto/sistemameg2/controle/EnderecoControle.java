package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Endereco;
import com.projeto.sistemameg2.repositorios.EnderecoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EnderecoControle {
    @Autowired
    private EnderecoRepositorio enderecoRepositorio;

    @GetMapping("/cadastroEndereco")
    public ModelAndView cadastrar(Endereco endereco) {
        ModelAndView mv = new ModelAndView("administrativo/enderecos/cadastro");
        mv.addObject("endereco", endereco);
        return mv;
    }

    @PostMapping("/salvarEndereco")
    public ModelAndView salvar(Endereco endereco, BindingResult result) {
        if (result.hasErrors()) {

            return cadastrar(endereco);// redireciona após salvar
        }
        enderecoRepositorio.saveAndFlush(endereco);
        return cadastrar(new Endereco());


    }
}

