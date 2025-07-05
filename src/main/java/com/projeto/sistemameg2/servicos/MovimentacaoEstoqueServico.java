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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional
    public MovimentacaoEstoqueDTO salvar(MovimentacaoEstoqueDTO dto) {
        Produto produto = produtoRepositorio.findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        Usuario usuario = usuarioRepositorio.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        movimentacao.setTipo(dto.getTipo());
        movimentacao.setQuantidade(dto.getQuantidade());
        movimentacao.setData(LocalDateTime.now());

        int novaQuantidadeEstoque = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0;

        if (movimentacao.getTipo() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA) {
            novaQuantidadeEstoque += movimentacao.getQuantidade();
        } else if (movimentacao.getTipo() == MovimentacaoEstoque.TipoMovimentacao.SAIDA) {
            if (novaQuantidadeEstoque < movimentacao.getQuantidade()) {
                throw new RuntimeException("Quantidade em estoque insuficiente para a saída.");
            }
            novaQuantidadeEstoque -= movimentacao.getQuantidade();
        }

        produto.setQuantidadeEstoque(novaQuantidadeEstoque);
        produtoRepositorio.save(produto);

        movimentacaoRepositorio.save(movimentacao);

        return MovimentacaoEstoqueDTO.fromEntity(movimentacao);
    }
@Transactional
public Optional<MovimentacaoEstoqueDTO> atualizar(Long id, MovimentacaoEstoqueDTO dto) {
    Optional<MovimentacaoEstoque> optMov = movimentacaoRepositorio.findById(id);

    if (optMov.isEmpty()) {
        return Optional.empty();
    }

    MovimentacaoEstoque movimentacaoExistente = optMov.get();

    // Reverte o impacto da movimentação antiga no estoque
    Produto produtoAntigo = movimentacaoExistente.getProduto();
    int quantidadeAntiga = movimentacaoExistente.getQuantidade();
    if (movimentacaoExistente.getTipo() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA) {
        produtoAntigo.setQuantidadeEstoque(produtoAntigo.getQuantidadeEstoque() - quantidadeAntiga);
    } else {
        produtoAntigo.setQuantidadeEstoque(produtoAntigo.getQuantidadeEstoque() + quantidadeAntiga);
    }
    produtoRepositorio.save(produtoAntigo);

    // Busca os novos dados
    Produto novoProduto = produtoRepositorio.findById(dto.getProdutoId())
            .orElseThrow(() -> new RuntimeException("Produto não encontrado."));
    Usuario novoUsuario = usuarioRepositorio.findById(dto.getUsuarioId())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

    movimentacaoExistente.setProduto(novoProduto);
    movimentacaoExistente.setUsuario(novoUsuario);
    movimentacaoExistente.setTipo(dto.getTipo());
    movimentacaoExistente.setQuantidade(dto.getQuantidade());
    movimentacaoExistente.setData(LocalDateTime.now());

    // Aplica o impacto da nova movimentação no estoque
    int novaQuantidadeEstoque = novoProduto.getQuantidadeEstoque() != null ? novoProduto.getQuantidadeEstoque() : 0;
    if (movimentacaoExistente.getTipo() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA) {
        novaQuantidadeEstoque += movimentacaoExistente.getQuantidade();
    } else {
        if (novaQuantidadeEstoque < movimentacaoExistente.getQuantidade()) {
            throw new RuntimeException("Quantidade em estoque insuficiente para a saída.");
        }
        novaQuantidadeEstoque -= movimentacaoExistente.getQuantidade();
    }
    novoProduto.setQuantidadeEstoque(novaQuantidadeEstoque);
    produtoRepositorio.save(novoProduto);

    movimentacaoRepositorio.save(movimentacaoExistente);

    return Optional.of(MovimentacaoEstoqueDTO.fromEntity(movimentacaoExistente));
}
@Transactional
public boolean deletar(Long id) {
    Optional<MovimentacaoEstoque> optMov = movimentacaoRepositorio.findById(id);
    if (optMov.isEmpty()) {
        return false; // Não encontrou a movimentação para deletar
    }

    MovimentacaoEstoque movimentacao = optMov.get();
    Produto produto = movimentacao.getProduto();

    // Reverter o impacto da movimentação no estoque do produto
    int quantidadeAtualEstoque = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0;

    if (movimentacao.getTipo() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA) {
        quantidadeAtualEstoque -= movimentacao.getQuantidade();
    } else if (movimentacao.getTipo() == MovimentacaoEstoque.TipoMovimentacao.SAIDA) {
        quantidadeAtualEstoque += movimentacao.getQuantidade();
    }

    if (quantidadeAtualEstoque < 0) {
        throw new RuntimeException("Não é possível excluir a movimentação pois resultaria em estoque negativo.");
    }

    produto.setQuantidadeEstoque(quantidadeAtualEstoque);
    produtoRepositorio.save(produto);

    movimentacaoRepositorio.delete(movimentacao);

    return true;
}

}
