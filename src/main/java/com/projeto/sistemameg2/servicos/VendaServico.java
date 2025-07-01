package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.dto.ItemVendaDTO;
import com.projeto.sistemameg2.dto.VendaDTO;
import com.projeto.sistemameg2.dto.VendaMensalDTO;
import com.projeto.sistemameg2.modelos.Cliente;
import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.modelos.Venda;
import com.projeto.sistemameg2.modelos.ItemVenda; // Se você tiver uma entidade ItemVenda
import com.projeto.sistemameg2.modelos.Usuario; // Se o usuário que registrou a venda for usado
import com.projeto.sistemameg2.repositorios.ClienteRepositorio;
import com.projeto.sistemameg2.repositorios.ProdutoRepositorio;
import com.projeto.sistemameg2.repositorios.VendaRepositorio;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio; // Para buscar o usuário pelo ID

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Para garantir transação completa

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private ProdutoRepositorio produtoRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio; // Para buscar o usuário que registrou a venda

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Método para mapear Entidade para DTO
    private VendaDTO toDTO(Venda venda) {
        VendaDTO dto = new VendaDTO();
        dto.setId(venda.getId());
        dto.setClienteId(venda.getCliente().getId()); // Assume que Cliente não é nulo
        dto.setUsuarioId(venda.getUsuario().getId()); // Assume que Usuario não é nulo
        dto.setFormaPagamento(venda.getFormaPagamento());
        dto.setValorTotal(venda.getValorTotal());
        dto.setDataVenda(venda.getDataVenda().format(formatter));

        // Mapear itens da venda para ItemVendaDTO
        List<ItemVendaDTO> itemDTOs = venda.getItensVenda().stream().map(item -> {
            ItemVendaDTO itemDto = new ItemVendaDTO();
            itemDto.setProdutoId(item.getProduto().getId());
            itemDto.setNomeProduto(item.getProduto().getNome());
            itemDto.setQuantidade(item.getQuantidade());
            itemDto.setPrecoUnitario(item.getPrecoUnitario());
            itemDto.setSubTotal(item.getSubTotal());
            return itemDto;
        }).collect(Collectors.toList());
        dto.setItens(itemDTOs);

        return dto;
    }

    // Método para listar todas as vendas, retornando DTOs
    public List<VendaDTO> listarTodos() {
        return vendaRepositorio.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Método para buscar venda por ID, retornando DTO
    public Optional<VendaDTO> buscarPorId(Long id) {
        return vendaRepositorio.findById(id).map(this::toDTO);
    }
    
    // Método para obter totais de vendas por dia para o gráfico
    public List<VendaMensalDTO> obterTotaisPorDia() {
        // Implemente a lógica aqui para buscar os dados do banco e mapear para VendaMensalDTO
        // Isso geralmente envolve uma query JPQL ou nativa para agrupar por data
        // Exemplo:
        // List<Object[]> resultados = vendaRepositorio.sumVendasByDate();
        // return resultados.stream()
        //     .map(row -> new VendaMensalDTO((String) row[0], (Double) row[1])) // Assumindo tipo de retorno
        //     .collect(Collectors.toList());
        return List.of(); // Placeholder, implemente a lógica real
    }

    @Transactional // Garante que a transação seja completa (salvar venda e atualizar estoque)
    public VendaDTO salvar(VendaDTO vendaDTO) {
        // 1. Validar cliente e usuário
        Cliente cliente = clienteRepositorio.findById(vendaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
        Usuario usuario = usuarioRepositorio.findById(vendaDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário (vendedor) não encontrado."));

        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setUsuario(usuario); // Setar o usuário que registrou a venda
        venda.setFormaPagamento(vendaDTO.getFormaPagamento());
        venda.setDataVenda(LocalDateTime.now()); // Data gerada no backend

        BigDecimal valorTotalCalculado = BigDecimal.ZERO;
        // Lista para armazenar os itens da venda (entidades)
        List<ItemVenda> itensVenda = new java.util.ArrayList<>();

        // 2. Processar itens e calcular total
        for (ItemVendaDTO itemDTO : vendaDTO.getItens()) {
            Produto produto = produtoRepositorio.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemDTO.getProdutoId()));

            if (produto.getQuantidadeEstoque() < itemDTO.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            // Cria o ItemVenda
            ItemVenda itemVenda = new ItemVenda();
            itemVenda.setProduto(produto);
            itemVenda.setQuantidade(itemDTO.getQuantidade());
            itemVenda.setPrecoUnitario(produto.getPreco()); // Preço do produto no momento da venda
            itemVenda.setVenda(venda); // Liga o item à venda

            BigDecimal subTotal = produto.getPreco().multiply(new BigDecimal(itemDTO.getQuantidade()));
            itemVenda.setSubTotal(subTotal);
            
            itensVenda.add(itemVenda); // Adiciona à lista de itens da venda
            valorTotalCalculado = valorTotalCalculado.add(subTotal);

            // Atualizar estoque do produto
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemDTO.getQuantidade());
            produtoRepositorio.save(produto); // Salva o produto com estoque atualizado
        }
        
        venda.setItensVenda(itensVenda); // Seta a lista de itens na venda principal
        venda.setValorTotal(valorTotalCalculado);

        // NOVO: Persistir o valor recebido se a forma de pagamento for Dinheiro
        if ("Dinheiro".equalsIgnoreCase(vendaDTO.getFormaPagamento())) {
            venda.setValorRecebido(vendaDTO.getValorRecebido());
        } else {
            venda.setValorRecebido(null); // Garante que seja nulo para outras formas de pagamento
        }

        Venda vendaSalva = vendaRepositorio.save(venda);
        return toDTO(vendaSalva); // Retorna o DTO da venda salva
    }

    // Método para criar Venda (removido porque 'salvar' já faz isso)
    // public Venda criarVenda(VendaDTO vendaDTO) { ... }

    // Atualizar uma venda existente (se for permitido e tiver lógica)
    @Transactional
    public Optional<VendaDTO> atualizar(Long id, VendaDTO vendaDTO) {
        // A atualização de vendas é complexa devido ao estoque.
        // Se precisar implementar, considere a reversão do estoque antigo
        // e o ajuste do novo estoque.
        // Por enquanto, lança UnsupportedOperationException como placeholder.
        throw new UnsupportedOperationException("Atualização de venda não implementada ou permitida para evitar inconsistências de estoque.");
        // Exemplo:
        // return vendaRepositorio.findById(id).map(vendaExistente -> {
        //    // Lógica de atualização complexa aqui (reverter estoque antigo, aplicar novo estoque, etc.)
        //    return toDTO(vendaRepositorio.save(vendaExistente));
        // });
    }

    // Deletar uma venda (e reverter estoque)
    @Transactional
    public boolean deletar(Long id) {
        Optional<Venda> vendaOpt = vendaRepositorio.findById(id);
        if (vendaOpt.isPresent()) {
            Venda venda = vendaOpt.get();

            // Reverter estoque dos produtos
            for (ItemVenda item : venda.getItensVenda()) {
                Produto produto = item.getProduto();
                if (produto != null) {
                    produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
                    produtoRepositorio.save(produto);
                }
            }
            vendaRepositorio.delete(venda);
            return true;
        }
        return false;
    }
}