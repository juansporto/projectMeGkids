
package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.modelos.Categoria;
import com.projeto.sistemameg2.repositorios.CategoriaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServico {

    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    public List<Categoria> listarTodos() {
        return categoriaRepositorio.findAll();
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepositorio.findById(id);
    }

    public Categoria salvar(Categoria categoria) {
        return categoriaRepositorio.save(categoria);
    }

    public Optional<Categoria> atualizar(Long id, Categoria novaCategoria) {
        return categoriaRepositorio.findById(id).map(categoria -> {
            categoria.setNome(novaCategoria.getNome());
            // Adicione outros campos se houver
            return categoriaRepositorio.save(categoria);
        });
    }

    public boolean deletar(Long id) {
        if (categoriaRepositorio.existsById(id)) {
            categoriaRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}
