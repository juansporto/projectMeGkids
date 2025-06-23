package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.modelos.ItemVenda;
import com.projeto.sistemameg2.repositorios.ItemVendaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemVendaServico {

    @Autowired
    private ItemVendaRepositorio itemVendaRepositorio;

    public List<ItemVenda> listarTodos() {
        return itemVendaRepositorio.findAll();
    }

    public Optional<ItemVenda> buscarPorId(Long id) {
        return itemVendaRepositorio.findById(id);
    }

    // Este salvar e atualizar geralmente não seriam usados diretamente para o fluxo principal de venda,
    // pois os itens são salvos via VendaService (cascata).
    // Podem ser úteis para testes ou operações muito específicas.
    public ItemVenda salvar(ItemVenda itemVenda) {
        return itemVendaRepositorio.save(itemVenda);
    }

    public Optional<ItemVenda> atualizar(Long id, ItemVenda novoItemVenda) {
        return itemVendaRepositorio.findById(id).map(itemVenda -> {
            itemVenda.setProduto(novoItemVenda.getProduto());
            itemVenda.setQuantidade(novoItemVenda.getQuantidade());
            itemVenda.setVenda(novoItemVenda.getVenda()); // Cuidado ao mudar a venda de um item
            itemVenda.setPrecoUnitario(novoItemVenda.getPrecoUnitario());
            itemVenda.setSubTotal(novoItemVenda.getSubTotal()); // Adicionar subTotal
            return itemVendaRepositorio.save(itemVenda);
        });
    }

    public boolean deletar(Long id) {
        if (itemVendaRepositorio.existsById(id)) {
            itemVendaRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}