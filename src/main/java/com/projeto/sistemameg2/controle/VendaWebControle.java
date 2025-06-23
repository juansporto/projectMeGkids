package com.projeto.sistemameg2.controle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // Este é um controlador para HTML
@RequestMapping("/admin/vendas") // A URL para acessar esta página via navegador
public class VendaWebControle {

    @GetMapping
    public String exibirPaginaVendas() {
        return "admin/vendas"; // Nome do arquivo HTML que vamos criar
    }
}