package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Cliente;
import com.projeto.sistemameg2.servicos.ClienteServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/clientes")
public class ClienteControle {

    @Autowired
    private ClienteServico clienteServico;

    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteServico.listarTodos());
        return "admin/clienteslista";
    }

    @GetMapping("/novo")
    public String novoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "admin/clientesform";
    }

    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteServico.buscarPorId(id).orElse(null);
        if (cliente == null) {
            return "redirect:/admin/clientes";
        }
        model.addAttribute("cliente", cliente);
        return "admin/clientesform";
    }

    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute Cliente cliente) {
        clienteServico.salvar(cliente);
        return "redirect:/admin/clientes";
    }

    @GetMapping("/deletar/{id}")
    public String deletarCliente(@PathVariable Long id) {
        clienteServico.deletar(id);
        return "redirect:/admin/clientes";
    }
}
