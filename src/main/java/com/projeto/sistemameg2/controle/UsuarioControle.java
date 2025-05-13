package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;




@Controller
public class UsuarioControle {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping("/cadastroUsuario")
    public String cadastroUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "administrativo/usuarios/cadastro";
    }

    @GetMapping("/listarUsuario")
    public String listarUsuario(Model model) {
        model.addAttribute("usuarios", usuarioRepositorio.findAll());
        return "administrativo/usuarios/lista";
    }

    @PostMapping("/salvarUsuario")
    public String salvarUsuario(@ModelAttribute Usuario usuario) {
        usuarioRepositorio.save(usuario);
        return "redirect:/listarUsuario";
    }

    @GetMapping("/editarUsuario/{id}")
    public String editarUsuario(@PathVariable("id") Long id, Model model) {
        Usuario usuario = usuarioRepositorio.findById(id).orElse(null);
        model.addAttribute("usuario", usuario);
        return "administrativo/usuarios/cadastro";
    }

    @GetMapping("/removerUsuario/{id}")
    public String removerUsuario(@PathVariable("id") Long id) {
        usuarioRepositorio.deleteById(id);
        return "redirect:/listarUsuario";
    }
}

