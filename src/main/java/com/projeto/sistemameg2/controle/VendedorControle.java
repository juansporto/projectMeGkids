package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Cliente;
import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.modelos.Usuario; // Certifique-se de que esta classe está correta
import com.projeto.sistemameg2.servicos.ClienteServico;
import com.projeto.sistemameg2.servicos.ProdutoServico;
import com.projeto.sistemameg2.servicos.UsuarioServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // Importação para Spring Security
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/vendedor")
public class VendedorControle {

    @Autowired
    private UsuarioServico usuarioServico;

    @Autowired
    private ClienteServico clienteServico;

    @Autowired
    private ProdutoServico produtoServico; // Certifique-se de que este serviço está correto

    @GetMapping("/dashboard")
    public String dashboardVendedor(Model model) {
        return "vendedor/dashboard";
    }

    @GetMapping("/clientes")
    public String listarClientesParaVendedor(Model model) {
        model.addAttribute("clientes", clienteServico.listarTodos());
        return "vendedor/clienteslista";
    }

    // MODIFICAÇÃO AQUI: Passando o ID do usuário logado para a página de vendas
    @GetMapping("/vendas")
    public String registrarVenda(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String userEmail = authentication.getName(); // Pega o email do usuário logado
            Optional<Usuario> usuarioLogado = usuarioServico.findByEmail(userEmail);
            usuarioLogado.ifPresent(usuario -> model.addAttribute("currentUserId", usuario.getId()));
        }
        return "vendedor/vendas";
    }

@GetMapping("/produtos")
public String listarProdutos(Model model) {
    List<Produto> produtos = produtoServico.listarTodos();
    model.addAttribute("produtos", produtos); // <- obrigatório
    return "vendedor/produtoslista";
}

    // Já estava passando o currentUserId aqui, mantemos assim
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
        Cliente cliente = clienteServico.buscarPorId(id).orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        model.addAttribute("cliente", cliente);
        return "vendedor/clientesform";
    }

    @GetMapping("/clientes/deletar/{id}")
    public String deletarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean deletado = clienteServico.deletar(id);
            if (deletado) {
                redirectAttributes.addFlashAttribute("message", "Cliente excluído com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Cliente não encontrado ou não foi possível excluir.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado ao excluir cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/vendedor/clientes";
    }
}