package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Cliente;
import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.servicos.ClienteServico;
import com.projeto.sistemameg2.servicos.UsuarioServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List; // Importação adicionada para List
import java.util.Optional;

@Controller
@RequestMapping("/vendedor")
public class VendedorControle {

    @Autowired
    private UsuarioServico usuarioServico;

    @Autowired
    private ClienteServico clienteServico;

    @GetMapping("/dashboard")
    public String dashboardVendedor(Model model) {
        return "vendedor/dashboard"; 
    }

    @GetMapping("/clientes")
    public String listarClientesParaVendedor(Model model) {
        model.addAttribute("clientes", clienteServico.listarTodos());
        return "vendedor/clienteslista";
    }

    @GetMapping("/vendas")
    public String registrarVenda() {
        return "vendedor/vendas"; 
    }

    @GetMapping("/produtos")
    public String consultarProdutos() {
        return "vendedor/produtos_consulta"; 
    }

    @GetMapping("/minhas-vendas")
    public String minhasVendas(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String userEmail = authentication.getName();
            Optional<Usuario> usuarioLogado = usuarioServico.findByEmail(userEmail);
            usuarioLogado.ifPresent(usuario -> model.addAttribute("currentUserId", usuario.getId()));
        }
        return "vendedor/minhas_vendas";
    }

    @GetMapping("/clientes/novo")
    public String novoClienteVendedor(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "vendedor/clientesform"; 
    }

    @PostMapping("/clientes/salvar")
    public String salvarClienteVendedor(Cliente cliente) {
        clienteServico.salvar(cliente);
        return "redirect:/vendedor/clientes";
    }
    
    @GetMapping("/clientes/editar/{id}")
    public String editarClienteForm(@PathVariable Long id, Model model) {
        // Certifique-se de que buscarPorId retorna Cliente, ou lide com Optional
        Cliente cliente = clienteServico.buscarPorId(id).orElseThrow(() -> new RuntimeException("Cliente não encontrado")); 
        model.addAttribute("cliente", cliente);
        return "vendedor/clientesform"; 
    }

    @GetMapping("/clientes/deletar/{id}")
    public String deletarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Chamando o método 'deletar' que retorna boolean
            boolean deletado = clienteServico.deletar(id); 
            if (deletado) {
                redirectAttributes.addFlashAttribute("message", "Cliente excluído com sucesso!");
            } else {
                // Se deletar retornar false, o cliente não foi encontrado
                redirectAttributes.addFlashAttribute("errorMessage", "Cliente não encontrado ou não foi possível excluir.");
            }
        } catch (Exception e) {
            // Captura qualquer outra exceção inesperada
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado ao excluir cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/vendedor/clientes"; 
    }
}