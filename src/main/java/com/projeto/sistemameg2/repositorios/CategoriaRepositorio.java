package com.projeto.sistemameg2.repositorios;



import org.springframework.data.jpa.repository.JpaRepository;
import com.projeto.sistemameg2.modelos.Categoria;

public interface CategoriaRepositorio extends JpaRepository<Categoria, Long> {
}
