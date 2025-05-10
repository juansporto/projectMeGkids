package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrincipalControle {


    @GetMapping("/admin/home")
    public String homeAdmin(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            return "administrativo/home";
        } else {
            return "redirect:/login"; // Redireciona se não estiver logado
        }
    }
}
