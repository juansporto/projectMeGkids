package com.projeto.sistemameg2.controle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/movimentacoes") // Esta é a URL para a página HTML no painel de admin
public class MovimentacaoWebControle {

    @GetMapping
    public String exibirPaginaMovimentacoes() {
        return "admin/movimentacoes"; // Retorna o nome do seu arquivo HTML
    }
}