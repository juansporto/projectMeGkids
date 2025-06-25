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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "vendedor/dashboard"; // Garante que a view do dashboard do vendedor seja retornada
    }

    @GetMapping("/clientes")
    public String listarClientesParaVendedor(Model model) {
        model.addAttribute("clientes", clienteServico.listarTodos());
        return "vendedor/clienteslista"; // Você precisa ter um arquivo 'clienteslista.html' em 'templates/vendedor/'
    }

    @GetMapping("/vendas")
    public String registrarVenda() {
        return "vendedor/vendas"; // CORREÇÃO AQUI: Retorna a página de vendas da pasta do VENDEDOR
    }

    @GetMapping("/produtos")
    public String consultarProdutos() {
        // CORREÇÃO AQUI: Um vendedor não deveria ver o estoque completo do admin.
        // Se precisar de uma tela de consulta de produtos para o vendedor, crie uma específica:
        return "vendedor/produtos_consulta"; // Exemplo: Você precisará criar esta view
        // Ou se não houver necessidade, remova este método e o link da navbar do vendedor.
    }

    @GetMapping("/minhas-vendas")
    public String minhasVendas(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String userEmail = authentication.getName();
            Optional<Usuario> usuarioLogado = usuarioServico.findByEmail(userEmail);
            usuarioLogado.ifPresent(usuario -> model.addAttribute("currentUserId", usuario.getId()));
        }
        return "vendedor/minhas_vendas"; // Você precisa ter esta página em 'templates/vendedor/'
    }

    @GetMapping("/clientes/novo")
    public String novoClienteVendedor(Model model) {
        model.addAttribute("cliente", new Cliente());
        // Se 'admin/clientesform' não tem elementos específicos de gerente, pode reutilizar.
        // Caso contrário, crie 'vendedor/clientesform.html'
        return "admin/clientesform"; // Mantenha assim por enquanto se for aceitável.
    }

    @PostMapping("/clientes/salvar")
    public String salvarClienteVendedor(Cliente cliente) {
        clienteServico.salvar(cliente);
        return "redirect:/vendedor/clientes";
    }
}