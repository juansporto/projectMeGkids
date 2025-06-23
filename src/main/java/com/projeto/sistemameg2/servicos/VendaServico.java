package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.dto.ItemVendaDTO;
import com.projeto.sistemameg2.dto.VendaDTO;
import com.projeto.sistemameg2.modelos.Cliente;
import com.projeto.sistemameg2.modelos.ItemVenda;
import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.modelos.Venda;
import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.modelos.MovimentacaoEstoque;
import com.projeto.sistemameg2.repositorios.ClienteRepositorio;
import com.projeto.sistemameg2.repositorios.ItemVendaRepositorio;
import com.projeto.sistemameg2.repositorios.ProdutoRepositorio;
import com.projeto.sistemameg2.repositorios.VendaRepositorio;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio;
import com.projeto.sistemameg2.repositorios.MovimentacaoEstoqueRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante!

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VendaServico {

    @Autowired
    private VendaRepositorio vendaRepositorio;
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ProdutoRepositorio produtoRepositorio;
    @Autowired
    private ItemVendaRepositorio itemVendaRepositorio; // Para garantir o save de itens
    @Autowired
    private MovimentacaoEstoqueRepositorio movimentacaoEstoqueRepositorio; // Para registrar saídas de estoque

    // Formatter para converter LocalDateTime para String no DTO de retorno
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


    // Listar todas as vendas (agora retornando DTOs)
    public List<VendaDTO> listarTodos() {
        return vendaRepositorio.findAll().stream()
                .map(this::convertToVendaDTO) // Usa método auxiliar
                .collect(Collectors.toList());
    }

    // Buscar venda por ID (agora retornando DTO)
    public Optional<VendaDTO> buscarPorId(Long id) {
        return vendaRepositorio.findById(id)
                .map(this::convertToVendaDTO); // Usa método auxiliar
    }

    @Transactional // Garante que tudo dentro deste método seja uma única transação
    public VendaDTO salvar(VendaDTO vendaDTO) {
        // 1. Busca Entidades Relacionadas
        Cliente cliente = clienteRepositorio.findById(vendaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + vendaDTO.getClienteId()));
        Usuario usuario = usuarioRepositorio.findById(vendaDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + vendaDTO.getUsuarioId()));

        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setUsuario(usuario);
        venda.setDataVenda(LocalDateTime.now()); // Data da venda é sempre o momento do registro
        venda.setFormaPagamento(vendaDTO.getFormaPagamento());
        venda.setValorTotal(BigDecimal.ZERO); // Inicializa para cálculo

        List<ItemVenda> itensVenda = new ArrayList<>();
        BigDecimal valorTotalVenda = BigDecimal.ZERO;

        if (vendaDTO.getItens() == null || vendaDTO.getItens().isEmpty()) {
            throw new RuntimeException("A venda deve conter pelo menos um item.");
        }

        // 2. Processa Itens de Venda, Valida Estoque e Atualiza
        for (ItemVendaDTO itemDto : vendaDTO.getItens()) {
            Produto produto = produtoRepositorio.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto com ID " + itemDto.getProdutoId() + " não encontrado."));

            if (produto.getQuantidadeEstoque() == null || produto.getQuantidadeEstoque() < itemDto.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome() + ". Quantidade solicitada: " + itemDto.getQuantidade() + ", disponível: " + produto.getQuantidadeEstoque());
            }

            // Cria o ItemVenda
            ItemVenda itemVenda = new ItemVenda();
            itemVenda.setVenda(venda); // Associa o item à venda (parent)
            itemVenda.setProduto(produto);
            itemVenda.setQuantidade(itemDto.getQuantidade());

            // Define o preço unitário do produto NO MOMENTO DA VENDA (importante para histórico)
            itemVenda.setPrecoUnitario(produto.getPreco());
            itemVenda.setSubTotal(produto.getPreco().multiply(BigDecimal.valueOf(itemDto.getQuantidade())));

            itensVenda.add(itemVenda);
            valorTotalVenda = valorTotalVenda.add(itemVenda.getSubTotal());

            // 3. ATUALIZA O ESTOQUE DO PRODUTO (SAÍDA)
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemDto.getQuantidade());
            produtoRepositorio.save(produto); // Salva a alteração do produto no BD

            // 4. REGISTRA A MOVIMENTAÇÃO DE SAÍDA NO HISTÓRICO
            MovimentacaoEstoque movSaida = new MovimentacaoEstoque();
            movSaida.setProduto(produto);
            movSaida.setUsuario(usuario); // Usuário que realizou a venda
            movSaida.setTipo(MovimentacaoEstoque.TipoMovimentacao.SAIDA);
            movSaida.setQuantidade(itemDto.getQuantidade());
            movimentacaoEstoqueRepositorio.save(movSaida); // A data é definida pelo @PrePersist no modelo MovimentacaoEstoque
        }

        venda.setItens(itensVenda); // Associa a lista de itens à venda
        venda.setValorTotal(valorTotalVenda); // Define o valor total calculado

        // 5. Salva a Venda (o cascade=ALL em Venda garante que os itensVenda também serão salvos)
        Venda vendaSalva = vendaRepositorio.save(venda);

        return convertToVendaDTO(vendaSalva); // Retorna o DTO da venda salva
    }

    @Transactional
    public Optional<VendaDTO> atualizar(Long id, VendaDTO vendaDTO) {
        // ATENÇÃO: A atualização de vendas é EXTREMAMENTE complexa e raramente é uma boa ideia.
        // Sistemas de vendas geralmente preferem cancelar/estornar uma venda e criar uma nova,
        // para manter o histórico financeiro e de estoque.
        // Se você precisa dessa funcionalidade, ela exigirá um controle meticuloso:
        // 1. Reverter o impacto no estoque dos itens ORIGINAIS da venda.
        // 2. Processar os NOVOS itens (validar estoque, baixar estoque).
        // 3. Atualizar a própria venda e seus itens.
        // POR ISSO, este método está apenas como um ESQUELETO. Considere fortemente
        // o padrão de estorno/cancelamento em vez de atualização direta.

        throw new UnsupportedOperationException("A atualização direta de vendas não é recomendada ou implementada sem lógica de negócio complexa.");
        /*
        Optional<Venda> optVenda = vendaRepositorio.findById(id);
        if (optVenda.isEmpty()) {
            return Optional.empty();
        }

        Venda vendaExistente = optVenda.get();

        // Lógica de reversão de estoque da venda existente (complexa)
        // ...

        Cliente cliente = clienteRepositorio.findById(vendaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
        Usuario usuario = usuarioRepositorio.findById(vendaDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        vendaExistente.setCliente(cliente);
        vendaExistente.setUsuario(usuario);
        vendaExistente.setFormaPagamento(vendaDTO.getFormaPagamento());
        // dataVenda não deve ser alterada ou precisa de lógica específica
        // valorTotal e itens seriam reprocessados.

        Venda vendaAtualizada = vendaRepositorio.save(vendaExistente);
        return Optional.of(convertToVendaDTO(vendaAtualizada));
        */
    }

    @Transactional
    public boolean deletar(Long id) {
        // ATENÇÃO: Deletar uma venda tem grande impacto no estoque e histórico financeiro.
        // Em vez de deletar, considere mudar o status da venda para "CANCELADA"
        // e/ou criar uma movimentação de estoque de "ESTORNO".
        // A lógica abaixo tenta reverter o estoque dos itens da venda e registrar entrada.

        Optional<Venda> optVenda = vendaRepositorio.findById(id);
        if (optVenda.isEmpty()) {
            return false;
        }

        Venda venda = optVenda.get();
        // Usar o usuário da venda para registrar o estorno, ou um usuário específico para estornos.
        Usuario usuario = venda.getUsuario();

        // Itera sobre os itens da venda para reverter o estoque e registrar movimentação de entrada
        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            // Adiciona a quantidade de volta ao estoque
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
            produtoRepositorio.save(produto); // Salva a atualização do produto

            // Registra a movimentação de ENTRADA (estorno)
            MovimentacaoEstoque movEntrada = new MovimentacaoEstoque();
            movEntrada.setProduto(produto);
            movEntrada.setUsuario(usuario); // Pode ser o usuário que cancelou/deletou
            movEntrada.setTipo(MovimentacaoEstoque.TipoMovimentacao.ENTRADA);
            movEntrada.setQuantidade(item.getQuantidade());
            movimentacaoEstoqueRepositorio.save(movEntrada);
        }

        // Deleta a venda (e os itens em cascata por causa do CascadeType.ALL)
        vendaRepositorio.deleteById(id);
        return true;
    }

    // --- Métodos Auxiliares de Conversão ---
    private VendaDTO convertToVendaDTO(Venda venda) {
        VendaDTO dto = new VendaDTO();
        dto.setId(venda.getId());
        dto.setClienteId(venda.getCliente() != null ? venda.getCliente().getId() : null);
        dto.setUsuarioId(venda.getUsuario() != null ? venda.getUsuario().getId() : null);
        dto.setDataVenda(venda.getDataVenda().format(FORMATTER)); // Formata a data
        dto.setFormaPagamento(venda.getFormaPagamento());
        dto.setValorTotal(venda.getValorTotal());

        if (venda.getItens() != null) {
            List<ItemVendaDTO> itemDtos = venda.getItens().stream().map(this::convertToItemVendaDTO).collect(Collectors.toList());
            dto.setItens(itemDtos);
        }
        return dto;
    }

    private ItemVendaDTO convertToItemVendaDTO(ItemVenda itemVenda) {
        ItemVendaDTO itemDto = new ItemVendaDTO();
        itemDto.setProdutoId(itemVenda.getProduto() != null ? itemVenda.getProduto().getId() : null);
        itemDto.setQuantidade(itemVenda.getQuantidade());
        // Não incluímos precoUnitario e subTotal no ItemVendaDTO de entrada,
        // mas eles podem ser incluídos no DTO de SAÍDA se necessário.
        // Por exemplo, você pode ter um ItemVendaResponseDTO
        return itemDto;
    }
}