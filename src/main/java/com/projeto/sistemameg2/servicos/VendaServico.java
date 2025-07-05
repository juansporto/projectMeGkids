package com.projeto.sistemameg2.servicos;

import com.projeto.sistemameg2.dto.ItemVendaDTO;
import com.projeto.sistemameg2.dto.VendaDTO;
import com.projeto.sistemameg2.dto.VendaDiariaDTO;
import com.projeto.sistemameg2.dto.MovimentacaoEstoqueDTO;
import com.projeto.sistemameg2.modelos.*;
import com.projeto.sistemameg2.repositorios.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
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
    @Autowired
    private MovimentacaoEstoqueServico movimentacaoEstoqueServico;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");

    private VendaDTO toDTO(Venda venda) {
        VendaDTO dto = new VendaDTO();
        dto.setId(venda.getId());
        dto.setClienteId(venda.getCliente().getId());
        dto.setUsuarioId(venda.getUsuario().getId());
        dto.setFormaPagamento(venda.getFormaPagamento());
        dto.setValorTotal(venda.getValorTotal());
        dto.setDataVenda(venda.getDataVenda().format(formatter));

        List<ItemVendaDTO> itensDto = venda.getItensVenda().stream().map(item -> {
            ItemVendaDTO itemDto = new ItemVendaDTO();
            itemDto.setProdutoId(item.getProduto().getId());
            itemDto.setNomeProduto(item.getProduto().getNome());
            itemDto.setQuantidade(item.getQuantidade());
            itemDto.setPrecoUnitario(item.getPrecoUnitario());
            itemDto.setSubTotal(item.getSubTotal());
            return itemDto;
        }).collect(Collectors.toList());
        dto.setItens(itensDto);

        return dto;
    }

    public List<VendaDTO> listarTodos() {
        return vendaRepositorio.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<VendaDTO> buscarPorId(Long id) {
        return vendaRepositorio.findById(id).map(this::toDTO);
    }

    public List<VendaDiariaDTO> obterTotaisVendasDiariasSemana() {
        LocalDateTime agora = LocalDateTime.now();

        LocalDateTime inicioSemana = agora.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                                         .toLocalDate().atStartOfDay();
        LocalDateTime fimSemana = agora.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
                                        .toLocalDate().atTime(23, 59, 59);

        List<Object[]> resultados = vendaRepositorio.sumVendasByDay(inicioSemana, fimSemana);

        Map<String, BigDecimal> vendasPorDia = new LinkedHashMap<>();
        LocalDate dataIteracao = inicioSemana.toLocalDate();
        while (!dataIteracao.isAfter(fimSemana.toLocalDate())) {
            vendasPorDia.put(dataIteracao.format(dayFormatter), BigDecimal.ZERO);
            dataIteracao = dataIteracao.plusDays(1);
        }

        for (Object[] row : resultados) {
            String dia = (String) row[0];
            BigDecimal total = (BigDecimal) row[1];
            if (total != null) {
                vendasPorDia.put(dia, total);
            }
        }

        List<VendaDiariaDTO> listaOrdenada = vendasPorDia.entrySet().stream()
                .map(entry -> new VendaDiariaDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(VendaDiariaDTO::getDia, (d1, d2) -> {
                    LocalDate data1 = LocalDate.parse(d1 + "/" + LocalDate.now().getYear(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalDate data2 = LocalDate.parse(d2 + "/" + LocalDate.now().getYear(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    return data1.compareTo(data2);
                }))
                .collect(Collectors.toList());

        return listaOrdenada;
    }

    @Transactional
    public VendaDTO salvar(VendaDTO vendaDTO) {
        Cliente cliente = clienteRepositorio.findById(vendaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
        Usuario usuario = usuarioRepositorio.findById(vendaDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário (vendedor) não encontrado."));

        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setUsuario(usuario);
        venda.setFormaPagamento(vendaDTO.getFormaPagamento());
        venda.setDataVenda(LocalDateTime.now());

        BigDecimal valorTotalCalculado = BigDecimal.ZERO;
        List<ItemVenda> itensVenda = new ArrayList<>();

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

            // Registra movimentação de saída no estoque
            MovimentacaoEstoqueDTO movDto = new MovimentacaoEstoqueDTO();
            movDto.setProdutoId(produto.getId());
            movDto.setQuantidade(itemDTO.getQuantidade());
            movDto.setTipo(MovimentacaoEstoque.TipoMovimentacao.SAIDA);
            movDto.setUsuarioId(usuario.getId());
            movimentacaoEstoqueServico.salvar(movDto);
        }

        venda.setItensVenda(itensVenda);
        venda.setValorTotal(valorTotalCalculado);

        if ("Dinheiro".equalsIgnoreCase(vendaDTO.getFormaPagamento())) {
            venda.setValorRecebido(vendaDTO.getValorRecebido());
        } else {
            venda.setValorRecebido(null);
        }

        Venda vendaSalva = vendaRepositorio.save(venda);
        return toDTO(vendaSalva);
    }

    @Transactional
    public Optional<VendaDTO> atualizar(Long id, VendaDTO vendaDTO) {
        // Atualizar venda é complexo por causa do estoque e movimentações
        // Exemplo: precisamos reverter estoque antigo, deletar movimentações antigas,
        // salvar novas movimentações e atualizar venda
        return vendaRepositorio.findById(id).map(vendaExistente -> {
            // 1. Reverter movimentações antigas (estorno do estoque)
            for (ItemVenda item : vendaExistente.getItensVenda()) {
                MovimentacaoEstoqueDTO movEstorno = new MovimentacaoEstoqueDTO();
                movEstorno.setProdutoId(item.getProduto().getId());
                movEstorno.setQuantidade(item.getQuantidade());
                movEstorno.setTipo(MovimentacaoEstoque.TipoMovimentacao.ENTRADA); // Estorno
                movEstorno.setUsuarioId(vendaExistente.getUsuario().getId());
                movimentacaoEstoqueServico.salvar(movEstorno);
            }
            // 2. Deletar os itens antigos da venda (dependendo do seu modelo, pode ser cascade)

            // 3. Atualizar dados da venda
            Cliente cliente = clienteRepositorio.findById(vendaDTO.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
            Usuario usuario = usuarioRepositorio.findById(vendaDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuário (vendedor) não encontrado."));

            vendaExistente.setCliente(cliente);
            vendaExistente.setUsuario(usuario);
            vendaExistente.setFormaPagamento(vendaDTO.getFormaPagamento());
            vendaExistente.setDataVenda(LocalDateTime.now());

            BigDecimal valorTotalCalculado = BigDecimal.ZERO;
            List<ItemVenda> novosItens = new ArrayList<>();

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
                itemVenda.setVenda(vendaExistente);

                BigDecimal subTotal = produto.getPreco().multiply(new BigDecimal(itemDTO.getQuantidade()));
                itemVenda.setSubTotal(subTotal);

                novosItens.add(itemVenda);
                valorTotalCalculado = valorTotalCalculado.add(subTotal);

                // Registrar movimentação de saída para o estoque
                MovimentacaoEstoqueDTO movDto = new MovimentacaoEstoqueDTO();
                movDto.setProdutoId(produto.getId());
                movDto.setQuantidade(itemDTO.getQuantidade());
                movDto.setTipo(MovimentacaoEstoque.TipoMovimentacao.SAIDA);
                movDto.setUsuarioId(usuario.getId());
                movimentacaoEstoqueServico.salvar(movDto);
            }

            vendaExistente.setItensVenda(novosItens);
            vendaExistente.setValorTotal(valorTotalCalculado);

            if ("Dinheiro".equalsIgnoreCase(vendaDTO.getFormaPagamento())) {
                vendaExistente.setValorRecebido(vendaDTO.getValorRecebido());
            } else {
                vendaExistente.setValorRecebido(null);
            }

            Venda vendaAtualizada = vendaRepositorio.save(vendaExistente);
            return toDTO(vendaAtualizada);
        });
    }

    @Transactional
    public boolean deletar(Long id) {
        Optional<Venda> vendaOpt = vendaRepositorio.findById(id);
        if (vendaOpt.isPresent()) {
            Venda venda = vendaOpt.get();

            // Reverter estoque (entrada dos itens da venda)
            for (ItemVenda item : venda.getItensVenda()) {
                MovimentacaoEstoqueDTO movEstorno = new MovimentacaoEstoqueDTO();
                movEstorno.setProdutoId(item.getProduto().getId());
                movEstorno.setQuantidade(item.getQuantidade());
                movEstorno.setTipo(MovimentacaoEstoque.TipoMovimentacao.ENTRADA);
                movEstorno.setUsuarioId(venda.getUsuario().getId());
                movimentacaoEstoqueServico.salvar(movEstorno);
            }

            vendaRepositorio.delete(venda);
            return true;
        }
        return false;
    }
}
