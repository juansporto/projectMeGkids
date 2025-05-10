package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CadastroControle {

    @GetMapping("/cadastro")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "administrativo/cadastro"; // Corrigido o nome do template
    }

    @PostMapping("/cadastro")
    public String processarCadastro(Usuario usuario) {
        // Aqui você pode adicionar a lógica para salvar o usuário no banco de dados
        // Por exemplo: usuarioRepository.save(usuario);

        return "redirect:/login"; // Redireciona para a página de login após o cadastro
    }
}
