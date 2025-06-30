package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.dto.ItemVendaDTO;
import com.projeto.sistemameg2.dto.VendaDTO;
import com.projeto.sistemameg2.dto.VendaMensalDTO;
import com.projeto.sistemameg2.modelos.Cliente;
import com.projeto.sistemameg2.modelos.Produto;
import com.projeto.sistemameg2.modelos.Venda;
import com.projeto.sistemameg2.modelos.ItemVenda;
import com.projeto.sistemameg2.modelos.Usuario;
import com.projeto.sistemameg2.repositorios.ClienteRepositorio;
import com.projeto.sistemameg2.repositorios.ProdutoRepositorio;
import com.projeto.sistemameg2.repositorios.VendaRepositorio;
import com.projeto.sistemameg2.repositorios.UsuarioRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private UsuarioRepositorio usuarioRepositorio;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Método para mapear Entidade para DTO
    private VendaDTO toDTO(Venda venda) {
        VendaDTO dto = new VendaDTO();
        dto.setId(venda.getId());
        
        // <--- TRATAMENTO DE NULOS MAIS ROBUSTO PARA CLIENTE E USUÁRIO
        if (venda.getCliente() != null) {
            dto.setClienteId(venda.getCliente().getId());
        } else {
            // Opcional: tratar erro ou definir um valor padrão
            dto.setClienteId(null); 
        }

        if (venda.getUsuario() != null) {
            dto.setUsuarioId(venda.getUsuario().getId());
        } else {
            // Opcional: tratar erro ou definir um valor padrão
            dto.setUsuarioId(null);
        }
        // ---> FIM DO TRATAMENTO DE NULOS

        dto.setFormaPagamento(venda.getFormaPagamento());
        dto.setValorTotal(venda.getValorTotal());
        dto.setDataVenda(venda.getDataVenda().format(formatter));
        dto.setValorRecebido(venda.getValorRecebido()); // Inclui o valor recebido no DTO de retorno

        // Mapear itens da venda para ItemVendaDTO
        List<ItemVendaDTO> itemDTOs = venda.getItensVenda().stream().map(item -> {
            ItemVendaDTO itemDto = new ItemVendaDTO();
            itemDto.setProdutoId(item.getProduto() != null ? item.getProduto().getId() : null); // Verifica nulo
            itemDto.setNomeProduto(item.getProduto() != null ? item.getProduto().getNome() : "Produto Desconhecido"); // Verifica nulo
            itemDto.setQuantidade(item.getQuantidade());
            itemDto.setPrecoUnitario(item.getPrecoUnitario());
            itemDto.setSubTotal(item.getSubTotal());
            return itemDto;
        }).collect(Collectors.toList());
        dto.setItens(itemDTOs);

        return dto;
    }

    // ... (restante dos métodos listarTodos, buscarPorId (para DTO), obterTotaisPorDia - inalterados) ...
    public List<VendaDTO> listarTodos() {
        return vendaRepositorio.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<VendaDTO> buscarPorId(Long id) {
        return vendaRepositorio.findById(id).map(this::toDTO);
    }
    
    public List<VendaMensalDTO> obterTotaisPorDia() {
        // Implemente a lógica aqui para buscar os dados do banco e mapear para VendaMensalDTO
        // Isso geralmente envolve uma query JPQL ou nativa para agrupar por data
        // return List.of(); // Placeholder, implemente a lógica real
        // Exemplo:
        // return vendaRepositorio.findDailySalesTotals(); // Crie este método no seu repositório
        return List.of();
    }


    @Transactional
    public VendaDTO salvar(VendaDTO vendaDTO) {
        // 1. Validar cliente e usuário (usando orElseThrow para extrair o valor)
        Cliente cliente = clienteRepositorio.findById(vendaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + vendaDTO.getClienteId()));
        Usuario usuario = usuarioRepositorio.findById(vendaDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário (vendedor) não encontrado com ID: " + vendaDTO.getUsuarioId()));

        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setUsuario(usuario);
        venda.setFormaPagamento(vendaDTO.getFormaPagamento());
        venda.setDataVenda(LocalDateTime.now());

        BigDecimal valorTotalCalculado = BigDecimal.ZERO;
        List<ItemVenda> itensVenda = new java.util.ArrayList<>();

        // 2. Processar itens e calcular total
        for (ItemVendaDTO itemDTO : vendaDTO.getItens()) {
            Produto produto = produtoRepositorio.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemDTO.getProdutoId()));

            if (produto.getQuantidadeEstoque() < itemDTO.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            ItemVenda itemVenda = new ItemVenda();
            itemVenda.setProduto(produto);
            itemVenda.setQuantidade(itemDTO.getQuantidade());
            itemVenda.setPrecoUnitario(produto.getPreco());
            itemVenda.setVenda(venda);

            BigDecimal subTotal = produto.getPreco().multiply(new BigDecimal(itemDTO.getQuantidade()));
            itemVenda.setSubTotal(subTotal);
            
            itensVenda.add(itemVenda);
            valorTotalCalculado = valorTotalCalculado.add(subTotal);

            // Atualizar estoque do produto
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemDTO.getQuantidade());
            produtoRepositorio.save(produto);
        }
        
        venda.setItensVenda(itensVenda);
        venda.setValorTotal(valorTotalCalculado);

        // Persistir o valor recebido se a forma de pagamento for Dinheiro
        if ("Dinheiro".equalsIgnoreCase(vendaDTO.getFormaPagamento()) && vendaDTO.getValorRecebido() != null) {
            venda.setValorRecebido(vendaDTO.getValorRecebido());
        } else {
            venda.setValorRecebido(null); 
        }

        Venda vendaSalva = vendaRepositorio.save(venda);
        return toDTO(vendaSalva); // Retorna o DTO da venda salva
    }

    // ... (restante dos métodos atualizar e deletar - inalterados ou conforme a última revisão) ...
    @Transactional
    public Optional<VendaDTO> atualizar(Long id, VendaDTO vendaDTO) {
        throw new UnsupportedOperationException("Atualização de venda não implementada ou permitida para evitar inconsistências de estoque.");
    }

    @Transactional
    public boolean deletar(Long id) {
        Optional<Venda> vendaOpt = vendaRepositorio.findById(id);
        if (vendaOpt.isPresent()) {
            Venda venda = vendaOpt.get();

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