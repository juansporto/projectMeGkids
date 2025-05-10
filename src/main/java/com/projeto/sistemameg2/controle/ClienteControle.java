package com.projeto.sistemameg2.controle;




import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteControle {

    @GetMapping("/cliente/home")
    public String homeCliente() {
        return "cliente/home";
    }

    @GetMapping("/cliente/cadastroPedido")
    public String cadastrarPedidoCliente() {
        return "cliente/pedido/cadastro";
    }

    @GetMapping("/cliente/listarPedidos")
    public String listarPedidosCliente() {
        return "cliente/pedido/lista";
    }

    // Adicione outras páginas específicas se necessário
}

