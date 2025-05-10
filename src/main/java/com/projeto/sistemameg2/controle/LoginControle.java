package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginControle {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping("/login")
    public String login() {
        return "administrativo/login";  // Retorna a tela de login
    }

    @PostMapping("/login")
    public String loginProcess(@RequestParam String email,
                               @RequestParam String senha,
                               Model model,
                               HttpSession session) {
        Optional<Usuario> usuarioOptional = usuarioRepositorio.findByEmail(email);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            if (usuario.getSenha().equals(senha)) {
                // Salva o usuário na sessão
                session.setAttribute("usuario", usuario);

                // Redireciona de acordo com o perfil
                if (usuario.getTipoPerfil() == Usuario.TipoPerfil.ADMIN) {
                    return "redirect:/admin/home";
                } else if (usuario.getTipoPerfil() == Usuario.TipoPerfil.CLIENTE) {
                    return "redirect:/cliente/home";
                }
            } else {
                model.addAttribute("erro", "Senha incorreta!");
                return "administrativo/login";
            }
        }

        model.addAttribute("erro", "Email não encontrado!");
        return "administrativo/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Destroi a sessão
        return "redirect:/login";
    }
}
