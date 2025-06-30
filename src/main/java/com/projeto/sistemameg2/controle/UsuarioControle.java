package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.servicos.UsuarioServico;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; // Remova @RestController aqui, se mantiver @Controller
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller // Mantenha @Controller para retornar views
@RequestMapping("/admin/usuarios")
public class UsuarioControle {

    @Autowired
    private UsuarioServico usuarioServico;

    // 1. Método para exibir a página HTML de usuários (GET /admin/usuarios)
    @GetMapping // Mapeia para GET /admin/usuarios
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioServico.listar());
        return "admin/usuarioslista";
    }

    @GetMapping("/novo")
    public String novoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("tipos", Usuario.TipoUsuario.values());
        return "admin/usuariosform";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioServico.buscarPorId(id).orElse(null);
        if (usuario == null) {
            return "redirect:/admin/usuarios";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("tipos", Usuario.TipoUsuario.values());
        return "admin/usuariosform";
    }

    @PostMapping("/salvar")
    public String salvarUsuario(@ModelAttribute Usuario usuario) {
        usuarioServico.salvar(usuario);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/deletar/{id}")
    public String deletarUsuario(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            boolean sucesso = usuarioServico.deletar(id);
            if (!sucesso) {
                redirectAttrs.addFlashAttribute("mensagemErro", "Usuário não encontrado.");
            } else {
                redirectAttrs.addFlashAttribute("mensagemSucesso", "Usuário excluído com sucesso.");
            }
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // 2. Método para a API REST de usuários (GET /admin/usuarios/api)
    // ALTERAÇÃO: Adicionado "/api" ao @GetMapping para desambiguar a rota
    @GetMapping("/api") // Mapeia para GET /admin/usuarios/api
    @ResponseBody // Indica que o retorno deve ser o corpo da resposta HTTP (JSON/XML), não uma view
    public List<Usuario> listarTodosUsuariosApi() {
        return usuarioServico.listar();
    }

    // 3. Método para a API REST para buscar um único usuário (GET /admin/usuarios/{id} - se quiser API)
    // Ou remova este se você já buscou pelo /editar/{id} para a view.
    // Se você usa o /editar/{id} para a view, este método de API pode causar conflito se o cliente buscar diretamente o ID.
    // Se quiser manter este como API, deve ser `@GetMapping("/api/{id}")`
    @GetMapping("/{id}") // CUIDADO: Este mapeamento pode conflitar se /editar/{id} também for usado como API
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        return usuarioServico.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}