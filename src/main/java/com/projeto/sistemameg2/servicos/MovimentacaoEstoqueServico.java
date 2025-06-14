package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.dto.MovimentacaoEstoqueDTO;
import com.projeto.sistemameg2.modelos.MovimentacaoEstoque;
import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.repositorios.MovimentacaoEstoqueRepositorio;
import com.projeto.sistemameg2.repositorios.ProdutoRepositorio;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovimentacaoEstoqueServico {

    @Autowired
    private MovimentacaoEstoqueRepositorio movimentacaoRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    public List<MovimentacaoEstoqueDTO> listarTodas() {
        return movimentacaoRepositorio.findAll().stream()
                .map(MovimentacaoEstoqueDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<MovimentacaoEstoqueDTO> buscarPorId(Long id) {
        return movimentacaoRepositorio.findById(id)
                .map(MovimentacaoEstoqueDTO::fromEntity);
    }

    public MovimentacaoEstoqueDTO salvar(MovimentacaoEstoqueDTO dto) {
        Produto produto = produtoRepositorio.findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Usuario usuario = usuarioRepositorio.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        movimentacao.setTipo(dto.getTipo());
        movimentacao.setQuantidade(dto.getQuantidade());

        movimentacaoRepositorio.save(movimentacao);

        return MovimentacaoEstoqueDTO.fromEntity(movimentacao);
    }

    public Optional<MovimentacaoEstoqueDTO> atualizar(Long id, MovimentacaoEstoqueDTO dto) {
        Optional<MovimentacaoEstoque> optMov = movimentacaoRepositorio.findById(id);

        if (optMov.isEmpty()) {
            return Optional.empty();
        }

        MovimentacaoEstoque movimentacao = optMov.get();

        Produto produto = produtoRepositorio.findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Usuario usuario = usuarioRepositorio.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        movimentacao.setTipo(dto.getTipo());
        movimentacao.setQuantidade(dto.getQuantidade());

        movimentacaoRepositorio.save(movimentacao);

        return Optional.of(MovimentacaoEstoqueDTO.fromEntity(movimentacao));
    }

    public boolean deletar(Long id) {
        if (movimentacaoRepositorio.existsById(id)) {
            movimentacaoRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}
