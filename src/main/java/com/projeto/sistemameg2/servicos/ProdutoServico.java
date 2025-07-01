// src/main/java/com/projeto/sistemameg2/servicos/ProdutoServico.java
package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.modelos.Categoria;
import com.projeto.sistemameg2.repositorios.ProdutoRepositorio;
import com.projeto.sistemameg2.repositorios.CategoriaRepositorio;
import com.projeto.sistemameg2.repositorios.ItemVendaRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoServico {

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    public List<Produto> listarTodos() {
        return produtoRepositorio.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepositorio.findById(id);
    }

    public Produto salvar(Produto produto) {
        // Lógica para carregar a Categoria do banco antes de salvar o Produto
        if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
            categoriaRepositorio.findById(produto.getCategoria().getId())
                                .ifPresent(produto::setCategoria);
        } else {
            produto.setCategoria(null); // Caso não haja categoria ou ID
        }
        return produtoRepositorio.save(produto);
    }

    public Optional<Produto> atualizar(Long id, Produto produtoAtualizado) {
        return produtoRepositorio.findById(id).map(produtoExistente -> {
            produtoExistente.setNome(produtoAtualizado.getNome());
            produtoExistente.setDescricao(produtoAtualizado.getDescricao());
            produtoExistente.setPreco(produtoAtualizado.getPreco());
            produtoExistente.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            produtoExistente.setCodigoBarras(produtoAtualizado.getCodigoBarras());
            
            // Lógica para carregar a Categoria do banco antes de atualizar
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

     @Autowired
    private ItemVendaRepositorio itemVendaRepositorio; // <<--- INJETE ESTE REPOSITÓRIO

    public boolean deletar(Long id) {
        if (produtoRepositorio.existsById(id)) {
            // <<--- VERIFICAÇÃO PARA CHAVE ESTRANGEIRA ---
            if (itemVendaRepositorio.existsByProdutoId(id)) { // Você precisará criar este método no ItemVendaRepositorio
                throw new IllegalStateException("Produto não pode ser excluído: possui vendas associadas. Considere desativá-lo.");
            }
            // <<--- FIM DA VERIFICAÇÃO ---

            produtoRepositorio.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Categoria> listarTodasCategorias() {
        return categoriaRepositorio.findAll();
    }
    
    // Método para buscar por código de barras
    public Optional<Produto> buscarPorCodigoBarras(String codigoBarras) {
        return produtoRepositorio.findByCodigoBarras(codigoBarras);
    }
    
}