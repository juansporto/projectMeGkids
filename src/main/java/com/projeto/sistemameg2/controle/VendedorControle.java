package com.projeto.sistemameg2.controle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vendedor")
public class VendedorControle {

    @GetMapping("/dashboard")
    public String dashboardVendedor() {
        return "vendedor/dashboard";
    }

    @GetMapping("/pedidos")
    public String listarPedidos() {
        return "vendedor/pedidos";
    }

    @GetMapping("/vendas/registrar")
    public String registrarVenda() {
        return "vendedor/registrarVenda";
    }
}


