package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.ItemPedido;
import com.projeto.sistemameg2.modelos.Pedido;
import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.repositorios.ItemPedidoRepositorio;
import com.projeto.sistemameg2.repositorios.PedidoRepositorio;
import com.projeto.sistemameg2.repositorios.ProdutoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ItemPedidoControle {

    @Autowired
    private ItemPedidoRepositorio itemPedidoRepositorio;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @GetMapping("/cadastroItemPedido")
    public String cadastroItemPedido(Model model) {
        model.addAttribute("itemPedido", new ItemPedido());
        model.addAttribute("pedidos", pedidoRepositorio.findAll());
        model.addAttribute("produtos", produtoRepositorio.findAll());
        return "administrativo/itenspedidos/cadastro";
    }

    @GetMapping("/listarItemPedido")
    public String listarItemPedido(Model model) {
        model.addAttribute("itensPedidos", itemPedidoRepositorio.findAll());
        return "administrativo/itenspedidos/lista";
    }

    @PostMapping("/salvarItemPedido")
    public String salvarItemPedido(@ModelAttribute ItemPedido itemPedido) {
        itemPedidoRepositorio.save(itemPedido);
        return "redirect:/listarItemPedido";
    }

    @GetMapping("/editarItemPedido/{id}")
    public String editarItemPedido(@PathVariable("id") Long id, Model model) {
        ItemPedido item = itemPedidoRepositorio.findById(id).orElse(null);
        model.addAttribute("itemPedido", item);
        model.addAttribute("pedidos", pedidoRepositorio.findAll());
        model.addAttribute("produtos", produtoRepositorio.findAll());
        return "administrativo/itenspedidos/cadastro";
    }

    @GetMapping("/removerItemPedido/{id}")
    public String removerItemPedido(@PathVariable("id") Long id) {
        itemPedidoRepositorio.deleteById(id);
        return "redirect:/listarItemPedido";
    }
}
