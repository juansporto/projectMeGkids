package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.repositorios.MovimentacaoEstoqueRepositorio;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServico {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private MovimentacaoEstoqueRepositorio movimentacaoEstoqueRepositorio;

    public List<Usuario> listar() {
        return usuarioRepositorio.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepositorio.findById(id);
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepositorio.save(usuario);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepositorio.findByEmail(email);
    }

    public Optional<Usuario> atualizar(Long id, Usuario novoUsuario) {
        return usuarioRepositorio.findById(id).map(usuario -> {
            usuario.setNome(novoUsuario.getNome());
            usuario.setEmail(novoUsuario.getEmail());
            usuario.setSenha(novoUsuario.getSenha());
            // Adicione mais campos conforme necessário
            return usuarioRepositorio.save(usuario);
        });
    }

    public boolean deletar(Long id) {
        if (!usuarioRepositorio.existsById(id)) {
            return false;
        }

        // Verifica se o usuário possui movimentações de estoque
        boolean temMovimentacoes = movimentacaoEstoqueRepositorio.existsByUsuarioId(id);
        if (temMovimentacoes) {
            throw new RuntimeException("Não é possível excluir: o usuário possui movimentações de estoque.");
        }

        usuarioRepositorio.deleteById(id);
        return true;
    }
}
