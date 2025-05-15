package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.ItemPedido;
import com.projeto.sistemameg2.modelos.Pedido;
import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.repositorios.ItemPedidoRepositorio;
import com.projeto.sistemameg2.repositorios.PedidoRepositorio;
import com.projeto.sistemameg2.repositorios.ProdutoRepositorio;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;
@Controller
public class PedidoControle {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private ItemPedidoRepositorio itemPedidoRepositorio;

    @GetMapping("/cadastroPedido")
    public String cadastroPedido(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("usuarios", usuarioRepositorio.findAll());
        model.addAttribute("produtos", produtoRepositorio.findAll());
        return "administrativo/pedidos/cadastro";
    }

    @PostMapping("/salvarPedido")
    public String salvarPedido(@ModelAttribute Pedido pedido,
                               @RequestParam("produtoId") Long produtoId,
                               @RequestParam("quantidade") int quantidade) {

        Produto produto = produtoRepositorio.findById(produtoId).orElse(null);
        if (produto == null) return "redirect:/cadastroPedido";

        BigDecimal preco = produto.getPreco();
        BigDecimal total = preco.multiply(BigDecimal.valueOf(quantidade));

        pedido.setData(LocalDateTime.now());
        pedido.setTotal(total);
        pedido.setStatus("Pendente");
        pedidoRepositorio.save(pedido);

        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPreco(preco);

        itemPedidoRepositorio.save(item);

        return "redirect:/listarPedido";
    }
}
