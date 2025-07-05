package com.projeto.sistemameg2.modelos;

// Importações de anotações JPA para mapeamento objeto-relacional
import jakarta.persistence.*;

/**
 * Representa a entidade Categoria no sistema.
 * Esta classe é mapeada para uma tabela no banco de dados usando JPA (Jakarta Persistence API).
 */
@Entity // Indica que esta classe é uma entidade JPA e será mapeada para uma tabela no banco de dados.
@Table(name = "categorias") // Especifica o nome da tabela correspondente no banco de dados.
                               // O nome 'categorias' (plural e minúsculas) é uma boa prática.
public class Categoria {

    @Id // Marca o campo 'id' como a chave primária da tabela.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configura a estratégia de geração do valor para a chave primária.
                                                    // IDENTITY significa que o banco de dados cuidará do auto-incremento.
    private Long id; // Atributo para armazenar o identificador único da categoria. 'Long' é um tipo comum para IDs.

    private String nome; // Atributo para armazenar o nome da categoria.

    // --- Métodos Getters e Setters ---
    // Estes métodos são essenciais para que o Spring Data JPA possa ler e escrever dados nos objetos Categoria,
    // e para que o Thymeleaf possa acessar os valores na camada de visualização.

    /**
     * Retorna o ID da categoria.
     * @return O ID da categoria.
     */
    public Long getId() {
        return id;
    }

    /**
     * Define o ID da categoria.
     * @param id O novo ID da categoria.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna o nome da categoria.
     * @return O nome da categoria.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome da categoria.
     * @param nome O novo nome da categoria.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }
}