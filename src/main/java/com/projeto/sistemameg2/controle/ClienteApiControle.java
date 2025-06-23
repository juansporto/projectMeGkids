package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Cliente;
import com.projeto.sistemameg2.servicos.ClienteServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes") // URL da API para Clientes
public class ClienteApiControle {

    @Autowired
    private ClienteServico clienteServico;

    @GetMapping
    public List<Cliente> listarTodosClientesApi() {
        return clienteServico.listarTodos(); // Retorna a lista de Clientes em JSON
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Long id) {
        return clienteServico.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}