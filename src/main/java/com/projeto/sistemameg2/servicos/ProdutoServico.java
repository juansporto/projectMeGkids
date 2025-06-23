package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.modelos.Categoria; // Importe a classe Categoria
import com.projeto.sistemameg2.repositorios.ProdutoRepositorio;
import com.projeto.sistemameg2.repositorios.CategoriaRepositorio; // Importe o CategoriaRepositorio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoServico {

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private CategoriaRepositorio categoriaRepositorio; // Injeção do repositório de categorias

    public List<Produto> listarTodos() {
        return produtoRepositorio.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepositorio.findById(id);
    }

    public Produto salvar(Produto produto) {
        return produtoRepositorio.save(produto);
    }

    public Optional<Produto> atualizar(Long id, Produto produtoAtualizado) {
        return produtoRepositorio.findById(id).map(produtoExistente -> {
            produtoExistente.setNome(produtoAtualizado.getNome());
            produtoExistente.setDescricao(produtoAtualizado.getDescricao());
            produtoExistente.setPreco(produtoAtualizado.getPreco());
            produtoExistente.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            produtoExistente.setCodigoBarras(produtoAtualizado.getCodigoBarras());
            // Certifique-se de que a categoria seja buscada do banco de dados para evitar TransientObjectException
            if (produtoAtualizado.getCategoria() != null && produtoAtualizado.getCategoria().getId() != null) {
                categoriaRepositorio.findById(produtoAtualizado.getCategoria().getId())
                        .ifPresent(produtoExistente::setCategoria);
            } else {
                produtoExistente.setCategoria(null); // Se nenhuma categoria for selecionada
            }
            produtoExistente.setStatus(produtoAtualizado.getStatus());
            return produtoRepositorio.save(produtoExistente);
        });
    }

    public boolean deletar(Long id) {
        if (produtoRepositorio.existsById(id)) {
            produtoRepositorio.deleteById(id);
            return true;
        }
        return false;
    }

    // Método para buscar todas as categorias, necessário para o formulário
    public List<Categoria> listarTodasCategorias() {
        return categoriaRepositorio.findAll();
    }
}