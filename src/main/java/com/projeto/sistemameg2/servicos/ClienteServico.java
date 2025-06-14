package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.modelos.Cliente;
import com.projeto.sistemameg2.repositorios.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServico {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    public List<Cliente> listarTodos() {
        return clienteRepositorio.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepositorio.findById(id);
    }

    public Cliente salvar(Cliente cliente) {
        return clienteRepositorio.save(cliente);
    }

    public Optional<Cliente> atualizar(Long id, Cliente novoCliente) {
        return clienteRepositorio.findById(id).map(cliente -> {
            cliente.setNome(novoCliente.getNome());
            cliente.setEmail(novoCliente.getEmail());
            cliente.setTelefone(novoCliente.getTelefone());
            // Adicione mais campos conforme necessário
            return clienteRepositorio.save(cliente);
        });
    }

    public boolean deletar(Long id) {
        if (clienteRepositorio.existsById(id)) {
            clienteRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}
