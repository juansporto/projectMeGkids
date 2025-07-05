package com.projeto.sistemameg2.servicos;

// Importações de classes e anotações do Spring Framework e modelos do projeto
import com.projeto.sistemameg2.modelos.Categoria;
import com.projeto.sistemameg2.repositorios.CategoriaRepositorio; // Importa a interface do repositório
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // Anotação para marcar esta classe como um serviço Spring.

import java.util.List;
import java.util.Optional; // Usado para lidar com a possível ausência de um objeto.

/**
 * Classe de Serviço para gerenciar operações de negócio relacionadas a Categorias.
 * Interage com o repositório para persistência de dados.
 */
@Service // Anotação que indica que esta classe é um componente de serviço Spring.
         // Ela encapsula a lógica de negócio e pode ser injetada em outros componentes (como controladores).
public class CategoriaServico {

    @Autowired // Injeção de dependência. O Spring Data JPA cria uma implementação concreta de CategoriaRepositorio em tempo de execução.
    private CategoriaRepositorio categoriaRepositorio;

    /**
     * Lista todas as categorias existentes no banco de dados.
     * @return Uma lista de objetos Categoria.
     */
    public List<Categoria> listarTodos() {
        // Chama o método findAll() fornecido pelo JpaRepository (via CategoriaRepositorio)
        // que busca todos os registros da tabela 'categorias' no banco de dados.
        return categoriaRepositorio.findAll();
    }

    /**
     * Busca uma categoria pelo seu ID.
     * @param id O ID da categoria a ser buscada.
     * @return Um Optional que pode conter a Categoria se encontrada, ou vazio se não.
     * Usar Optional é uma boa prática para evitar NullPointerExceptions.
     */
    public Optional<Categoria> buscarPorId(Long id) {
        // Chama o método findById() do repositório.
        return categoriaRepositorio.findById(id);
    }

    /**
     * Salva uma nova categoria ou atualiza uma existente.
     * Se o ID da categoria for nulo, o método irá inseri-la.
     * Se o ID for existente, o método irá atualizá-la.
     * @param categoria O objeto Categoria a ser salvo/atualizado.
     * @return A categoria salva (com o ID preenchido se for uma nova).
     */
    public Categoria salvar(Categoria categoria) {
        // O método save() do JpaRepository é versátil e lida com inserção e atualização.
        return categoriaRepositorio.save(categoria);
    }

    /**
     * Atualiza uma categoria existente.
     * Este método é uma alternativa explícita ao 'salvar' para atualizações.
     * Busca a categoria pelo ID, atualiza seu nome (e outros campos se houver), e então a salva.
     * @param id O ID da categoria a ser atualizada.
     * @param novaCategoria Um objeto Categoria contendo os novos dados (apenas o nome, neste caso).
     * @return Um Optional que pode conter a Categoria atualizada se encontrada, ou vazio se não.
     */
    public Optional<Categoria> atualizar(Long id, Categoria novaCategoria) {
        // Tenta encontrar a categoria pelo ID.
        // O .map() só é executado se o Optional contiver um valor.
        return categoriaRepositorio.findById(id).map(categoria -> {
            categoria.setNome(novaCategoria.getNome()); // Atualiza o nome da categoria existente com o novo nome.
            // Se houvessem outros campos na Categoria (ex: descrição), você os atualizaria aqui:
            // categoria.setDescricao(novaCategoria.getDescricao());
            return categoriaRepositorio.save(categoria); // Salva a categoria com os dados atualizados.
        });
    }

    /**
     * Deleta uma categoria pelo seu ID.
     * Verifica se a categoria existe antes de tentar deletar.
     * @param id O ID da categoria a ser deletada.
     * @return true se a categoria foi deletada com sucesso, false caso contrário (não encontrada).
     */
    public boolean deletar(Long id) {
        if (categoriaRepositorio.existsById(id)) { // Verifica se uma categoria com o ID existe no banco de dados.
            categoriaRepositorio.deleteById(id); // Deleta a categoria pelo ID.
            return true; // Retorna true para indicar sucesso na exclusão.
        }
        return false; // Retorna false se a categoria não foi encontrada e, portanto, não foi deletada.
    }
}