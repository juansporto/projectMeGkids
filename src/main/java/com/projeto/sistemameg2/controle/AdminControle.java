package com.projeto.sistemameg2.controle;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AdminControle {



    @GetMapping("/admin/cadastroUsuario")
    public String cadastrarUsuario() {
        return "administrativo/usuario/cadastro";
    }

    @GetMapping("/admin/listarUsuarios")
    public String listarUsuarios() {
        return "administrativo/usuario/lista";
    }

    @GetMapping("/admin/cadastroProduto")
    public String cadastrarProduto() {
        return "administrativo/produto/cadastro";
    }

    @GetMapping("/admin/listarProdutos")
    public String listarProdutos() {
        return "administrativo/produto/lista";
    }

    @GetMapping("/admin/cadastroPedido")
    public String cadastrarPedido() {
        return "administrativo/pedido/cadastro";
    }

    @GetMapping("/admin/listarPedidos")
    public String listarPedidos() {
        return "administrativo/pedido/lista";
    }
}
