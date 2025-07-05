package com.projeto.sistemameg2.controle;

// Importações de classes e anotações do Spring Framework e modelos do projeto
import com.projeto.sistemameg2.modelos.Categoria;
import com.projeto.sistemameg2.servicos.CategoriaServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; // Importa todas as anotações de mapeamento de requisições.

/**
 * Controlador Spring MVC para gerenciar as operações relacionadas a Categorias.
 * Lida com as requisições HTTP e interage com a camada de serviço.
 */
@Controller // Anotação que marca esta classe como um controlador Spring MVC.
@RequestMapping("/admin/categorias") // Define o caminho base (prefixo da URL) para todos os endpoints neste controlador.
                                    // Ex: Todas as rotas começarão com "/admin/categorias".
public class CategoriaControle {

    @Autowired // Anotação para Injeção de Dependência. O Spring "automaticamente" cria e injeta uma instância de CategoriaServico aqui.
               // Isso garante que o controlador tenha acesso aos métodos de lógica de negócio do serviço.
    private CategoriaServico categoriaServico;

    /**
     * Lida com requisições GET para a URL base "/admin/categorias".
     * Exibe a página de listagem de todas as categorias.
     * @param model Objeto Model para passar dados para a view (Thymeleaf).
     * @return O nome da view Thymeleaf a ser renderizada ("admin/categorias.html").
     */
    @GetMapping // Mapeia requisições GET para o caminho definido em @RequestMapping (ou seja, "/admin/categorias").
    public String listarCategorias(Model model) {
        // Adiciona a lista de todas as categorias ao objeto Model sob o atributo "categorias".
        // A view Thymeleaf poderá acessar essa lista usando `${categorias}`.
        model.addAttribute("categorias", categoriaServico.listarTodos());
        return "admin/categorias"; // Retorna o nome do template Thymeleaf que será exibido ao usuário.
    }

    /**
     * Lida com requisições GET para "/admin/categorias/nova".
     * Exibe o formulário para criar uma nova categoria.
     * @param model Objeto Model para passar dados para a view.
     * @return O nome da view Thymeleaf do formulário ("admin/categoria-form.html").
     */
    @GetMapping("/nova") // Mapeia requisições GET para "/admin/categorias/nova".
    public String mostrarFormularioNovaCategoria(Model model) {
        // Adiciona uma nova instância vazia de Categoria ao Model.
        // O formulário Thymeleaf usará este objeto para popular seus campos.
        model.addAttribute("categoria", new Categoria());
        return "admin/categoria-form"; // Retorna o template do formulário de categoria.
    }

    /**
     * Lida com requisições GET para "/admin/categorias/editar/{id}".
     * Exibe o formulário preenchido para editar uma categoria existente.
     * @param id O ID da categoria a ser editada, extraído da URL.
     * @param model Objeto Model para passar dados para a view.
     * @return O nome da view Thymeleaf do formulário, ou redireciona se a categoria não for encontrada.
     */
    @GetMapping("/editar/{id}") // Mapeia requisições GET com um ID variável na URL.
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        // @PathVariable extrai o valor 'id' da URL e o injeta como parâmetro do método.
        // Busca a categoria pelo ID. O .orElse(null) lida com o Optional, retornando null se não encontrar.
        Categoria categoria = categoriaServico.buscarPorId(id).orElse(null);
        if (categoria == null) {
            // Se a categoria não for encontrada, redireciona o usuário de volta para a lista de categorias.
            return "redirect:/admin/categorias";
        }
        // Adiciona a categoria encontrada ao Model, para que o formulário seja preenchido com seus dados.
        model.addAttribute("categoria", categoria);
        return "admin/categoria-form"; // Retorna o template do formulário.
    }

    /**
     * Lida com requisições POST para "/admin/categorias/salvar".
     * Recebe os dados do formulário e salva (ou atualiza) a categoria no banco de dados.
     * @param categoria O objeto Categoria preenchido com os dados do formulário (automaticamente via @ModelAttribute).
     * @return Um redirecionamento para a página de listagem de categorias.
     */
    @PostMapping("/salvar") // Mapeia requisições POST para "/admin/categorias/salvar".
    // @ModelAttribute vincula os dados enviados do formulário HTML a um objeto Categoria.
    // Se o 'id' do objeto categoria for nulo, o Spring Data JPA insere; se não, ele atualiza.
    public String salvarCategoria(@ModelAttribute Categoria categoria) {
        categoriaServico.salvar(categoria); // Chama o método salvar do serviço para persistir a categoria.
        // Redireciona o navegador para a URL de listagem de categorias. Isso evita o reenvio de formulário.
        return "redirect:/admin/categorias";
    }

    /**
     * Lida com requisições GET para "/admin/categorias/deletar/{id}".
     * Deleta uma categoria específica do banco de dados.
     * @param id O ID da categoria a ser deletada, extraído da URL.
     * @return Um redirecionamento para a página de listagem de categorias.
     */
    @GetMapping("/deletar/{id}") // Mapeia requisições GET para "/admin/categorias/deletar/{id}".
    public String deletarCategoria(@PathVariable Long id) {
        categoriaServico.deletar(id); // Chama o método deletar do serviço.
        return "redirect:/admin/categorias"; // Redireciona para a página de listagem.
    }
}