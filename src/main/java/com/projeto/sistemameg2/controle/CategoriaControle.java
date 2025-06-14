package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Categoria;
import com.projeto.sistemameg2.servicos.CategoriaServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categorias")
public class CategoriaControle {

    @Autowired
    private CategoriaServico categoriaServico;

    // Página de listagem de categorias
    @GetMapping
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaServico.listarTodos());
        return "admin/categorias";
    }

    // Página de formulário para nova categoria
    @GetMapping("/nova")
    public String mostrarFormularioNovaCategoria(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "admin/categoria-form";
    }

    // Página de formulário para editar categoria
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Categoria categoria = categoriaServico.buscarPorId(id).orElse(null);
        if (categoria == null) {
            return "redirect:/admin/categorias";
        }
        model.addAttribute("categoria", categoria);
        return "admin/categoria-form";
    }

    // Salvar ou atualizar categoria
    @PostMapping("/salvar")
    public String salvarCategoria(@ModelAttribute Categoria categoria) {
        categoriaServico.salvar(categoria);
        return "redirect:/admin/categorias";
    }

    // Deletar categoria
    @GetMapping("/deletar/{id}")
    public String deletarCategoria(@PathVariable Long id) {
        categoriaServico.deletar(id);
        return "redirect:/admin/categorias";
    }
}
