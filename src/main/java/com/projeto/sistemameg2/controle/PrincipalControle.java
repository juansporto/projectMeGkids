package com.projeto.sistemameg2.controle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrincipalControle {

    @GetMapping("/")
    public String home() {
        return "administrativo/home"; // O caminho para a página HTML dentro de templates
    }
}
