package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Pedido;
import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.repositorios.PedidoRepositorio;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PedidoControle {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping("/cadastroPedido")
    public String cadastroPedido(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("usuarios", usuarioRepositorio.findAll());
        return "administrativo/pedidos/cadastro";
    }

    @GetMapping("/listarPedido")
    public String listarPedido(Model model) {
        model.addAttribute("pedidos", pedidoRepositorio.findAll());
        return "administrativo/pedidos/lista";
    }

    @PostMapping("/salvarPedido")
    public String salvarPedido(@ModelAttribute Pedido pedido) {
        pedidoRepositorio.save(pedido);
        return "redirect:/listarPedido";
    }

    @GetMapping("/editarPedido/{id}")
    public String editarPedido(@PathVariable("id") Long id, Model model) {
        Pedido pedido = pedidoRepositorio.findById(id).orElse(null);
        model.addAttribute("pedido", pedido);
        model.addAttribute("usuarios", usuarioRepositorio.findAll());
        return "administrativo/pedidos/cadastro";
    }

    @GetMapping("/removerPedido/{id}")
    public String removerPedido(@PathVariable("id") Long id) {
        pedidoRepositorio.deleteById(id);
        return "redirect:/listarPedido";
    }
}
