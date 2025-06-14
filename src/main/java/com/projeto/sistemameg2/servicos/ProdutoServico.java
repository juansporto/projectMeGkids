package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.repositorios.ProdutoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoServico {

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    public List<Produto> listarTodos() {
        return produtoRepositorio.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepositorio.findById(id);
    }

    public Produto salvar(Produto produto) {
        return produtoRepositorio.save(produto);
    }

    public Optional<Produto> atualizar(Long id, Produto novoProduto) {
        return produtoRepositorio.findById(id).map(produto -> {
            produto.setNome(novoProduto.getNome());
            produto.setPreco(novoProduto.getPreco());
            produto.setDescricao(novoProduto.getDescricao());
            // Adicione mais campos conforme necessário
            return produtoRepositorio.save(produto);
        });
    }

    public boolean deletar(Long id) {
        if (produtoRepositorio.existsById(id)) {
            produtoRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}