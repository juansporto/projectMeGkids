package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.ItemPedido;
import com.projeto.sistemameg2.modelos.Pedido;
import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.modelos.Usuario;
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
import java.util.Optional;

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

    // Tela para criar novo pedido
    @GetMapping("/cadastroPedido")
    public String cadastroPedido(Model model) {
        model.addAttribute("usuarios", usuarioRepositorio.findAll());
        model.addAttribute("produtos", produtoRepositorio.findAll());
        return "administrativo/pedidos/cadastro";
    }

    // Salvar pedido novo
    @PostMapping("/salvarPedido")
    public String salvarPedido(
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("produtoId") Long produtoId,
            @RequestParam("quantidade") int quantidade) {

        Usuario usuario = usuarioRepositorio.findById(usuarioId).orElse(null);
        Produto produto = produtoRepositorio.findById(produtoId).orElse(null);

        if (usuario == null || produto == null) return "redirect:/cadastroPedido";

        BigDecimal preco = produto.getPreco();
        BigDecimal total = preco.multiply(BigDecimal.valueOf(quantidade));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
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

    // Listar todos os pedidos
    @GetMapping("/listarPedido")
    public String listarPedidos(Model model) {
        model.addAttribute("pedidos", pedidoRepositorio.findAll());
        return "administrativo/pedidos/lista";
    }

    // Tela para editar pedido
    @GetMapping("/administrativo/pedidos/editar/{id}")
    public String editarPedido(@PathVariable("id") Long id, Model model) {
        Pedido pedido = pedidoRepositorio.findById(id).orElse(null);
        if (pedido == null) {
            return "redirect:/listarPedido";
        }
        model.addAttribute("pedido", pedido);
        model.addAttribute("usuarios", usuarioRepositorio.findAll());
        model.addAttribute("produtos", produtoRepositorio.findAll());
        return "administrativo/pedidos/editar";
    }

    // Salvar edição do pedido
    @PostMapping("/administrativo/pedidos/editar")
    public String salvarEdicaoPedido(
            @RequestParam("id") Long id,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("status") String status) {

        Pedido pedido = pedidoRepositorio.findById(id).orElse(null);
        if (pedido == null) {
            return "redirect:/listarPedido";
        }

        Usuario usuario = usuarioRepositorio.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return "redirect:/listarPedido";
        }

        pedido.setUsuario(usuario);
        pedido.setStatus(status);
        pedidoRepositorio.save(pedido);

        return "redirect:/listarPedido";
    }

    // Deletar pedido
    @GetMapping("/administrativo/pedidos/deletar/{id}")
    public String deletarPedido(@PathVariable("id") Long id) {
        pedidoRepositorio.deleteById(id);
        return "redirect:/listarPedido";
    }
    @GetMapping("/administrativo/pedidos/finalizar/{id}")
    public String finalizarPedido(@PathVariable("id") Long id) {
        Optional<Pedido> optionalPedido = pedidoRepositorio.findById(id);
        if (optionalPedido.isPresent()) {
            Pedido pedido = optionalPedido.get();
            pedido.setStatus("Finalizado");
            pedidoRepositorio.save(pedido);
        }
        return "redirect:/administrativo/pedidos/listar";
    }

}
