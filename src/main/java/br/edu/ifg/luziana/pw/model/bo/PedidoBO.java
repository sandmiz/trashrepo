package br.edu.ifg.luziana.pw.model.bo;

import br.edu.ifg.luziana.pw.model.LogDTO;
import br.edu.ifg.luziana.pw.model.PedidoDTO;
import br.edu.ifg.luziana.pw.model.dao.PedidoDAO;
import br.edu.ifg.luziana.pw.model.dao.UsuarioDAO;
import br.edu.ifg.luziana.pw.model.dao.VeiculoDAO;
import br.edu.ifg.luziana.pw.model.entity.Pedido;
import br.edu.ifg.luziana.pw.model.entity.Usuario;
import br.edu.ifg.luziana.pw.model.entity.Veiculo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonNumber;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class PedidoBO {

    public static final String STATUS_PENDENTE = "pendente";
    public static final String STATUS_APROVADO = "aprovado";
    public static final String STATUS_REJEITADO = "rejeitado";

    @Inject
    PedidoDAO pedidoDAO;

    @Inject
    VeiculoDAO veiculoDAO;

    @Inject
    UsuarioDAO usuarioDAO;

    @Inject
    LogBO logBO;

    @Inject
    JsonWebToken jwt;

    public boolean validarEnvio(PedidoDTO dto) {
        if (dto == null) return false;
        if (dto.getVeiculoId() == null || dto.getVeiculoId() <= 0) return false;
        if (dto.getValorProposta() == null || dto.getValorProposta() < 1000) return false;
        if (dto.getFormaPagamento() == null || dto.getFormaPagamento().isBlank()) return false;
        return true;
    }

    public String mensagemErroEnvio(PedidoDTO dto) {
        if (dto == null) return "Dados do pedido não informados.";
        if (dto.getVeiculoId() == null || dto.getVeiculoId() <= 0) return "Selecione um veículo.";
        if (dto.getValorProposta() == null || dto.getValorProposta() < 1000) return "Valor da proposta deve ser de no mínimo R$ 1.000.";
        if (dto.getFormaPagamento() == null || dto.getFormaPagamento().isBlank()) return "Informe a forma de pagamento.";
        return null;
    }

    public void aplicarStatusPadrao(PedidoDTO dto) {
        if (dto != null && (dto.getStatus() == null || dto.getStatus().isBlank())) {
            dto.setStatus(STATUS_PENDENTE);
        }
    }

    public boolean podeAtualizarStatus(PedidoDTO dto) {
        if (dto == null || dto.getStatus() == null) return false;
        String s = dto.getStatus().toLowerCase();
        return STATUS_PENDENTE.equals(s) || STATUS_APROVADO.equals(s) || STATUS_REJEITADO.equals(s);
    }

    public boolean isStatusFinal(String status) {
        if (status == null) return false;
        String s = status.toLowerCase();
        return STATUS_APROVADO.equals(s) || STATUS_REJEITADO.equals(s);
    }

    @Transactional
    public Response enviar(PedidoDTO dto) {
        String erro = mensagemErroEnvio(dto);
        if (erro != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(erro).build();
        }
        Veiculo veiculo = veiculoDAO.findById(dto.getVeiculoId().intValue());
        if (veiculo == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Veículo não encontrado.").build();
        }
        Pedido entity = new Pedido();
        entity.setVeiculo(veiculo);
        entity.setValorProposta(dto.getValorProposta());
        entity.setFormaPagamento(dto.getFormaPagamento());
        entity.setMensagem(dto.getMensagem());
        entity.setStatus(STATUS_PENDENTE);
        entity.setCriadoEm(LocalDateTime.now());
        Usuario u = usuarioDAO.findById(((JsonNumber) jwt.getClaim("id")).intValue());
        if (u == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Usuário não encontrado.").build();
        }
        entity.setUsuario(u);

        pedidoDAO.insert(entity);

        LogDTO log = new LogDTO("PEDIDO_COMPRA",
                "Enviou proposta para veículo id=" + dto.getVeiculoId(),
                u.getNome(), u.getEmail(), "web");
        logBO.registrar(log);
        return Response.status(Response.Status.CREATED).entity(entity).build();
    }

    @Transactional
    public Response atualizarStatus(Integer id, String status) {
        Pedido entity = pedidoDAO.findByIdComRelacoes(id);
        if (entity == null) {
            entity = pedidoDAO.findById(id);
        }
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Pedido não encontrado.").build();
        }
        String s = status != null ? status.toLowerCase() : "";
        if (!STATUS_PENDENTE.equals(s) && !STATUS_APROVADO.equals(s) && !STATUS_REJEITADO.equals(s)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Status inválido.").build();
        }
        entity.setStatus(s);
        pedidoDAO.update(entity);
        return Response.ok(entity).build();
    }

    @Transactional
    public Response listar() {
        List<Pedido> lista = pedidoDAO.listarPorUsuario(((JsonNumber) jwt.getClaim("id")).intValue());
        return Response.ok(lista).build();
    }

    @Transactional
    public Response listarPorStatus(String status) {
        List<Pedido> lista = pedidoDAO.listarPorStatus(status);
        return Response.ok(lista).build();
    }
}
