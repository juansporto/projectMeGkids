package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Produto; // Entidade Produto
import com.projeto.sistemameg2.servicos.ProdutoServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // <--- Importante: Este é um REST Controller
@RequestMapping("/api/produtos") // <--- URL para a API de produtos
public class ProdutoApiControle {

    @Autowired
    private ProdutoServico produtoServico;

    @GetMapping
    public List<Produto> listarTodosProdutosApi() {
        return produtoServico.listarTodos();
    }

    // Você pode adicionar mais endpoints aqui para buscar um produto por ID, etc.
}