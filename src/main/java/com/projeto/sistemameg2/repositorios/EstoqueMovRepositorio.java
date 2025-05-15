package com.projeto.sistemameg2.repositorios;

import com.projeto.sistemameg2.modelos.EstoqueMov;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstoqueMovRepositorio extends JpaRepository<EstoqueMov, Long> { }
