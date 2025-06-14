package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.servicos.UsuarioServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioControle {

    @Autowired
    private UsuarioServico usuarioServico;

    @GetMapping
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
        return "admin/usuario-form";
    }

    @PostMapping("/salvar")
    public String salvarUsuario(@ModelAttribute Usuario usuario) {
        usuarioServico.salvar(usuario);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/deletar/{id}")
    public String deletarUsuario(@PathVariable Long id) {
        usuarioServico.deletar(id);
        return "redirect:/admin/usuarios";
    }
}
