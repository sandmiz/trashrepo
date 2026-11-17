package br.edu.ifg.luziana.pw.controller;

import br.edu.ifg.luziana.pw.model.LogDTO;
import br.edu.ifg.luziana.pw.model.PedidoDTO;
import br.edu.ifg.luziana.pw.model.bo.LogBO;
import br.edu.ifg.luziana.pw.model.bo.PedidoBO;
import br.edu.ifg.luziana.pw.model.dao.UsuarioDAO;
import br.edu.ifg.luziana.pw.model.entity.Usuario;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/pedido-compra")
public class PedidoController {

    @Inject
    PedidoBO pedidoBO;

    @Inject
    LogBO logBO;

    @Inject
    UsuarioDAO usuarioDAO;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance pedido();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance pedido() {
        return Templates.pedido();
    }

    @POST
    @Path("/enviar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response enviar(PedidoDTO dto, @QueryParam("usuarioId") Integer usuarioId) {
        Response resp = pedidoBO.enviar(dto, usuarioId);
        if (resp.getStatus() == 201) {
            try {
                String nome = null;
                String email = null;
                if (usuarioId != null) {
                    Usuario u = usuarioDAO.findById(usuarioId);
                    if (u != null) {
                        nome = u.getNome() + (u.getSobrenome() != null ? " " + u.getSobrenome() : "");
                        email = u.getEmail();
                    }
                }
                LogDTO log = new LogDTO("PEDIDO_COMPRA",
                        "Enviou proposta para veículo id=" + dto.getVeiculoId(),
                        nome, email, "web");
                logBO.registrar(log);
            } catch (Exception ignored) {
            }
        }
        return resp;
    }

    @GET
    @Path("/listar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listar(@QueryParam("usuarioId") Integer usuarioId) {
        if (usuarioId != null) {
            return pedidoBO.listarPorUsuario(usuarioId);
        }
        return pedidoBO.listar();
    }
}
