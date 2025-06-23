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
import org.springframework.transaction.annotation.Transactional; // Importe para garantir a atomicidade

import java.time.LocalDateTime; // Certifique-se de que está importado
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovimentacaoEstoqueServico {

    @Autowired
    private MovimentacaoEstoqueRepositorio movimentacaoRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio; // Certifique-se de que tem este repositório

    @Autowired
    private UsuarioRepositorio usuarioRepositorio; // Certifique-se de que tem este repositório

    public List<MovimentacaoEstoqueDTO> listarTodas() {
        return movimentacaoRepositorio.findAll().stream()
                .map(MovimentacaoEstoqueDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<MovimentacaoEstoqueDTO> buscarPorId(Long id) {
        return movimentacaoRepositorio.findById(id)
                .map(MovimentacaoEstoqueDTO::fromEntity);
    }

    @Transactional // Garante que a operação de salvar a movimentação e atualizar o produto seja atômica
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
        // A data é setada automaticamente pelo @PrePersist no modelo, não precisa setar aqui, a menos que venha do DTO

        // LÓGICA CHAVE: ATUALIZAR A QUANTIDADE NO PRODUTO
        int novaQuantidadeEstoque = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0; // Garante que não é null
        if (movimentacao.getTipo() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA) {
            novaQuantidadeEstoque += movimentacao.getQuantidade();
        } else if (movimentacao.getTipo() == MovimentacaoEstoque.TipoMovimentacao.SAIDA) {
            if (novaQuantidadeEstoque < movimentacao.getQuantidade()) {
                throw new RuntimeException("Quantidade em estoque insuficiente para a saída.");
            }
            novaQuantidadeEstoque -= movimentacao.getQuantidade();
        }
        produto.setQuantidadeEstoque(novaQuantidadeEstoque);
        produtoRepositorio.save(produto); // Salva a alteração na quantidade do produto

        // Salva o registro da movimentação APÓS atualizar o produto
        movimentacaoRepositorio.save(movimentacao);

        return MovimentacaoEstoqueDTO.fromEntity(movimentacao);
    }

    @Transactional // Garante a atomicidade
    public Optional<MovimentacaoEstoqueDTO> atualizar(Long id, MovimentacaoEstoqueDTO dto) {
        // ATENÇÃO: Atualizar movimentações de estoque pode ser complexo,
        // pois afeta o histórico e a quantidade atual do produto.
        // Geralmente, para correções, cria-se uma NOVA movimentação de estorno,
        // em vez de alterar uma existente.
        // Se você realmente precisa atualizar, a lógica abaixo é um ponto de partida,
        // mas considere as implicações para a integridade dos dados e histórico.

        Optional<MovimentacaoEstoque> optMov = movimentacaoRepositorio.findById(id);

        if (optMov.isEmpty()) {
            return Optional.empty();
        }

        MovimentacaoEstoque movimentacaoExistente = optMov.get();

        // Reverte o impacto da movimentação antiga na quantidade do produto
        Produto produtoAntigo = movimentacaoExistente.getProduto();
        int quantidadeAntiga = movimentacaoExistente.getQuantidade();
        if (movimentacaoExistente.getTipo() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA) {
            produtoAntigo.setQuantidadeEstoque(produtoAntigo.getQuantidadeEstoque() - quantidadeAntiga);
        } else { // SAIDA
            produtoAntigo.setQuantidadeEstoque(produtoAntigo.getQuantidadeEstoque() + quantidadeAntiga);
        }
        produtoRepositorio.save(produtoAntigo); // Salva a reversão

        // Agora, aplica o impacto da nova movimentação
        Produto novoProduto = produtoRepositorio.findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Novo Produto não encontrado."));
        Usuario novoUsuario = usuarioRepositorio.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Novo Usuário não encontrado."));

        movimentacaoExistente.setProduto(novoProduto);
        movimentacaoExistente.setUsuario(novoUsuario);
        movimentacaoExistente.setTipo(dto.getTipo());
        movimentacaoExistente.setQuantidade(dto.getQuantidade());
        movimentacaoExistente.setData(LocalDateTime.now()); // Opcional: atualizar a data da alteração

        int novaQuantidadeEstoque = novoProduto.getQuantidadeEstoque() != null ? novoProduto.getQuantidadeEstoque() : 0;
        if (movimentacaoExistente.getTipo() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA) {
            novaQuantidadeEstoque += movimentacaoExistente.getQuantidade();
        } else if (movimentacaoExistente.getTipo() == MovimentacaoEstoque.TipoMovimentacao.SAIDA) {
            if (novaQuantidadeEstoque < movimentacaoExistente.getQuantidade()) {
                throw new RuntimeException("Quantidade em estoque insuficiente para a nova saída.");
            }
            novaQuantidadeEstoque -= movimentacaoExistente.getQuantidade();
        }
        novoProduto.setQuantidadeEstoque(novaQuantidadeEstoque);
        produtoRepositorio.save(novoProduto); // Salva a nova quantidade do produto

        movimentacaoRepositorio.save(movimentacaoExistente); // Salva a movimentação atualizada

        return Optional.of(MovimentacaoEstoqueDTO.fromEntity(movimentacaoExistente));
    }

    @Transactional // Garante a atomicidade
    public boolean deletar(Long id) {
        // ATENÇÃO: Deletar movimentações de estoque também é complexo.
        // Considere criar um estorno em vez de deletar para manter o histórico.
        // A lógica abaixo tenta reverter a quantidade, mas é preciso ter cautela.

        Optional<MovimentacaoEstoque> optMov = movimentacaoRepositorio.findById(id);
        if (optMov.isEmpty()) {
            return false;
        }

        MovimentacaoEstoque movimentacao = optMov.get();
        Produto produto = movimentacao.getProduto();

        // Reverte o impacto da movimentação na quantidade do produto
        int quantidadeRevertida = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0;
        if (movimentacao.getTipo() == MovimentacaoEstoque.TipoMovimentacao.ENTRADA) {
            quantidadeRevertida -= movimentacao.getQuantidade();
        } else { // SAIDA
            quantidadeRevertida += movimentacao.getQuantidade();
        }
        // Opcional: Adicionar validação para não deixar a quantidade negativa se a reversão for uma saída que já deixou o estoque negativo
        if (quantidadeRevertida < 0) {
            // Isso pode indicar um problema de lógica ou dado, dependendo da sua regra de negócio
            System.err.println("Alerta: A reversão da exclusão resultaria em estoque negativo para o produto " + produto.getNome() + ". Id: " + produto.getId());
            // Você pode lançar uma exceção ou apenas logar
            // throw new RuntimeException("Não foi possível excluir a movimentação: reversão causaria estoque negativo.");
        }
        produto.setQuantidadeEstoque(quantidadeRevertida);
        produtoRepositorio.save(produto); // Salva a reversão no produto

        movimentacaoRepositorio.deleteById(id); // Deleta a movimentação
        return true;
    }
}