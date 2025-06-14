package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.modelos.Venda;
import com.projeto.sistemameg2.repositorios.VendaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendaServico {

    @Autowired
    private VendaRepositorio vendaRepositorio;

    public List<Venda> listarTodos() {
        return vendaRepositorio.findAll();
    }

    public Optional<Venda> buscarPorId(Long id) {
        return vendaRepositorio.findById(id);
    }

    public Venda salvar(Venda venda) {
        return vendaRepositorio.save(venda);
    }

    public Optional<Venda> atualizar(Long id, Venda novaVenda) {
        return vendaRepositorio.findById(id).map(venda -> {
            venda.setDataVenda(novaVenda.getDataVenda());
            venda.setCliente(novaVenda.getCliente());
            // Adicione mais campos conforme necessário
            return vendaRepositorio.save(venda);
        });
    }

    public boolean deletar(Long id) {
        if (vendaRepositorio.existsById(id)) {
            vendaRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}
