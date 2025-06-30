package com.projeto.sistemameg2.controle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // Este é um controlador para HTML (retorna nomes de view)
@RequestMapping("/admin/vendas") // A URL para acessar esta página via navegador
public class VendaWebControle {

    @GetMapping // Mapeia para GET /admin/vendas
    public String exibirPaginaVendas() {
        return "admin/vendas"; // Nome do arquivo HTML que exibe o formulário de venda e histórico
    }
    
    // Outros métodos para operações CRUD em vendas VIA HTML, se existirem.
    // Ex: @GetMapping("/novo"), @PostMapping("/salvar"), etc.
    // No seu caso, o formulário de venda já está na página 'admin/vendas', então talvez não precise de 'novo'.
    // Mas se você tivesse um formulário separado para 'novo', ele teria um GetMapping específico.
}