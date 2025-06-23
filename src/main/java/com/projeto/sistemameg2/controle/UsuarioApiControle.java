package com.projeto.sistemameg2.controle;

import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.servicos.UsuarioServico; // Supondo que você tem um UsuarioServico
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios") // URL da API para Usuários
public class UsuarioApiControle {

    @Autowired
    private UsuarioServico usuarioServico; // Injete seu serviço de usuário

    @GetMapping
    public List<Usuario> listarTodosUsuariosApi() {
        return usuarioServico.listar(); // Supondo um método 'listar()' no seu UsuarioServico
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        return usuarioServico.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}