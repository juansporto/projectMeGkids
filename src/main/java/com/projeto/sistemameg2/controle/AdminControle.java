package com.projeto.sistemameg2.controle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminControle {

    @GetMapping("/dashboard")
    public String dashboardGerente() {
        return "admin/dashboard";  // página principal do admin
    }
     @GetMapping("/relatorios")
    public String relatoriosPage() {
        return "admin/relatorios"; // Retorna o nome do template HTML
    }

    // Aqui só o que for geral para admin, nada de listar entidades específicas
}
