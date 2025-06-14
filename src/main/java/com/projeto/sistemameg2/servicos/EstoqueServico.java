package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.modelos.Estoque;
import com.projeto.sistemameg2.repositorios.EstoqueRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstoqueServico {

    @Autowired
    private EstoqueRepositorio estoqueRepositorio;

    public List<Estoque> listarTodos() {
        return estoqueRepositorio.findAll();
    }

    public Optional<Estoque> buscarPorId(Long id) {
        return estoqueRepositorio.findById(id);
    }

    public Estoque salvar(Estoque estoque) {
        return estoqueRepositorio.save(estoque);
    }

    public Optional<Estoque> atualizar(Long id, Estoque novoEstoque) {
        return estoqueRepositorio.findById(id).map(estoque -> {
            estoque.setProduto(novoEstoque.getProduto());
            estoque.setQuantidade(novoEstoque.getQuantidade());
            estoque.setLocalizacao(novoEstoque.getLocalizacao());
            // Adicione mais campos conforme necessário
            return estoqueRepositorio.save(estoque);
        });
    }

    public boolean deletar(Long id) {
        if (estoqueRepositorio.existsById(id)) {
            estoqueRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}
