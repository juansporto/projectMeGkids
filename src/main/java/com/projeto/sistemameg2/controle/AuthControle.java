package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthControle {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // GET /auth/login
    @GetMapping("/login")
    public String exibirLogin(@RequestParam(value = "erro", required = false) String erro,
                              @RequestParam(value = "logout", required = false) String logout,
                              Model model) {
        if (erro != null) {
            model.addAttribute("mensagemErro", "Email ou senha inválidos.");
        }
        if (logout != null) {
            model.addAttribute("mensagemSucesso", "Logout realizado com sucesso.");
        }
        return "auth/login"; // templates/auth/login.html
    }

    // GET /auth/registro
    @GetMapping("/registro")
    public String exibirFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    // POST /auth/registro
    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario, Model model) {
        if (usuarioRepositorio.existsByEmail(usuario.getEmail())) {
            model.addAttribute("mensagemErro", "Email já está em uso.");
            return "auth/registro";
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setAtivo(true);

        // Se o tipo não for definido no formulário, define como VENDEDOR
        if (usuario.getTipo() == null) {
            usuario.setTipo(Usuario.TipoUsuario.VENDEDOR);
        }

        usuarioRepositorio.save(usuario);

        model.addAttribute("mensagemSucesso", "Usuário registrado com sucesso!");
        model.addAttribute("usuario", new Usuario()); // limpa o formulário
        return "auth/registro";
    }
}
