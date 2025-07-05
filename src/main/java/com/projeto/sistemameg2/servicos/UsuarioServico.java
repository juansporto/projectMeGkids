package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.repositorios.MovimentacaoEstoqueRepositorio;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Importe este
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServico {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private MovimentacaoEstoqueRepositorio movimentacaoEstoqueRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder; // Adicione esta injeção!

    public List<Usuario> listar() {
        return usuarioRepositorio.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepositorio.findById(id);
    }

    // --- ALTERAÇÃO AQUI NO MÉTODO SALVAR ---
    public Usuario salvar(Usuario usuario) {
        // Verificar se é um novo usuário ou se a senha foi alterada (para evitar re-criptografar senhas já criptografadas)
        if (usuario.getId() == null || !usuario.getSenha().startsWith("$2a$") && !usuario.getSenha().isEmpty()) {
            // Se é um novo usuário OU a senha não está criptografada e não está vazia
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        return usuarioRepositorio.save(usuario);
    }
    // --- FIM DA ALTERAÇÃO ---

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepositorio.findByEmail(email);
    }

    // --- ATENÇÃO: VOCÊ TEM UM MÉTODO ATUALIZAR SEPARADO?
    // Se sim, ele também precisará do tratamento da senha.
    // O método 'salvar' acima pode lidar com isso se for usado tanto para criar quanto para atualizar.
    public Optional<Usuario> atualizar(Long id, Usuario novoUsuario) {
        return usuarioRepositorio.findById(id).map(usuarioExistente -> {
            usuarioExistente.setNome(novoUsuario.getNome());
            usuarioExistente.setEmail(novoUsuario.getEmail());
            // ATENÇÃO: Se a senha for atualizada, ela TAMBÉM precisa ser criptografada aqui
            if (novoUsuario.getSenha() != null && !novoUsuario.getSenha().isEmpty() && !novoUsuario.getSenha().startsWith("$2a$")) {
                 usuarioExistente.setSenha(passwordEncoder.encode(novoUsuario.getSenha()));
            }
            // Adicione mais campos conforme necessário
            return usuarioRepositorio.save(usuarioExistente);
        });
    }

    public boolean deletar(Long id) {
        if (!usuarioRepositorio.existsById(id)) {
            return false;
        }

        boolean temMovimentacoes = movimentacaoEstoqueRepositorio.existsByUsuarioId(id);
        if (temMovimentacoes) {
            throw new RuntimeException("Não é possível excluir: o usuário possui movimentações de estoque.");
        }

        usuarioRepositorio.deleteById(id);
        return true;
    }
}