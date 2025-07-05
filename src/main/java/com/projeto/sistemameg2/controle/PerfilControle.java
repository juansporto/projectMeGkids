// src/main/java/com/projeto/sistemameg2/controle/PerfilControle.java
package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.servicos.UsuarioServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // Para obter informações do usuário logado
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // Para receber dados do formulário
import org.springframework.web.bind.annotation.PostMapping; // Para salvar edições
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/perfil") // Rota base para a página de perfil
public class PerfilControle {

    @Autowired
    private UsuarioServico usuarioServico; // Seu serviço de usuário

    /**
     * Exibe a página de perfil do usuário logado.
     * @param model Objeto Model para passar dados para a view.
     * @param authentication Objeto Authentication do Spring Security com detalhes do usuário logado.
     * @return O nome da view Thymeleaf do perfil.
     */
    @GetMapping
    public String exibirPerfil(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // Redireciona para login se não estiver autenticado
            return "redirect:/auth/login";
        }

        String emailUsuarioLogado = authentication.getName(); // Spring Security geralmente retorna o email como "name"
        Optional<Usuario> usuarioOptional = usuarioServico.findByEmail(emailUsuarioLogado);

        if (usuarioOptional.isPresent()) {
            model.addAttribute("usuario", usuarioOptional.get());
            // Retorna o nome do arquivo HTML: perfil.html (assumindo em src/main/resources/templates/)
            return "perfil";
        } else {
            // Caso raro de usuário autenticado, mas não encontrado no banco
            model.addAttribute("mensagemErro", "Usuário não encontrado no sistema.");
            return "erro"; // Ou redirecione para uma página de erro genérica
        }
    }

    /**
     * Salva as alterações no perfil do usuário logado.
     * @param usuarioAtualizado Objeto Usuario com os dados do formulário.
     * @param authentication Objeto Authentication do Spring Security.
     * @param redirectAttributes Para adicionar mensagens flash após o redirecionamento.
     * @return Redireciona para a página de perfil.
     */
    @PostMapping("/salvar")
    public String salvarPerfil(@ModelAttribute Usuario usuarioAtualizado, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String emailUsuarioLogado = authentication.getName();
        Optional<Usuario> usuarioExistenteOpt = usuarioServico.findByEmail(emailUsuarioLogado);

        if (usuarioExistenteOpt.isPresent()) {
            Usuario usuarioExistente = usuarioExistenteOpt.get();
            // Atualiza apenas os campos permitidos para edição (nome, email, telefone, etc.)
            // NÃO permita que o usuário mude o ID, roles ou senha diretamente sem validação extra e segurança!
            usuarioExistente.setNome(usuarioAtualizado.getNome());
            // O email geralmente não é alterado diretamente por aqui ou requer validação extra
            // usuarioExistente.setEmail(usuarioAtualizado.getEmail()); // Descomente se permitir edição de email
            // Exemplo: se houver um campo de telefone na sua entidade Usuario
            // usuarioExistente.setTelefone(usuarioAtualizado.getTelefone());

            usuarioServico.salvar(usuarioExistente); // Usa o método salvar que também serve para atualizar
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Perfil atualizado com sucesso!");
            return "redirect:/perfil"; // Redireciona de volta para a página de perfil
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao atualizar perfil: usuário não encontrado.");
            return "redirect:/perfil"; // Permanece na página de perfil com erro
        }
    }

    /**
     * Endpoint para alterar a senha do usuário.
     * Esta é uma implementação básica e requer validações de segurança adicionais.
     * @param senhaAtual Senha atual digitada pelo usuário.
     * @param novaSenha Nova senha digitada pelo usuário.
     * @param confirmarNovaSenha Confirmação da nova senha.
     * @param authentication Objeto Authentication do Spring Security.
     * @param redirectAttributes Para adicionar mensagens flash.
     * @return Redireciona para a página de perfil.
     */
    @PostMapping("/alterar-senha")
    public String alterarSenha(@RequestParam String senhaAtual,
                               @RequestParam String novaSenha,
                               @RequestParam String confirmarNovaSenha,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String emailUsuarioLogado = authentication.getName();
        Optional<Usuario> usuarioOptional = usuarioServico.findByEmail(emailUsuarioLogado);

        if (usuarioOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuário não encontrado.");
            return "redirect:/perfil";
        }

        Usuario usuario = usuarioOptional.get();

        // 1. Validar senha atual (você precisará de um PasswordEncoder para comparar)
        // Exemplo: if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) { ... }
        // Por simplicidade, aqui é uma comparação direta (NÃO FAÇA ISSO EM PRODUÇÃO!)
        if (!usuario.getSenha().equals(senhaAtual)) { // APENAS PARA TESTE, SUBSTITUA POR PasswordEncoder
            redirectAttributes.addFlashAttribute("errorMessage", "Senha atual incorreta.");
            return "redirect:/perfil";
        }

        // 2. Validar nova senha
        if (!novaSenha.equals(confirmarNovaSenha)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nova senha e confirmação não coincidem.");
            return "redirect:/perfil";
        }
        if (novaSenha.length() < 6) { // Exemplo de validação de tamanho
            redirectAttributes.addFlashAttribute("errorMessage", "A nova senha deve ter pelo menos 6 caracteres.");
            return "redirect:/perfil";
        }

        // 3. Criptografar e salvar a nova senha
        // Exemplo: usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setSenha(novaSenha); // APENAS PARA TESTE, SUBSTITUA POR PasswordEncoder.encode()
        usuarioServico.salvar(usuario); // O método salvar também atualiza

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Senha alterada com sucesso!");
        return "redirect:/perfil";
    }
}