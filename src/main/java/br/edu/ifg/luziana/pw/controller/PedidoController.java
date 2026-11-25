package br.edu.ifg.luziana.pw.controller;

import br.edu.ifg.luziana.pw.model.LogDTO;
import br.edu.ifg.luziana.pw.model.PedidoDTO;
import br.edu.ifg.luziana.pw.model.bo.LogBO;
import br.edu.ifg.luziana.pw.model.bo.PedidoBO;
import br.edu.ifg.luziana.pw.model.dao.UsuarioDAO;
import br.edu.ifg.luziana.pw.model.entity.Usuario;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
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
    @Authenticated
    public Response enviar(PedidoDTO dto) {
        return pedidoBO.enviar(dto);
    }

    @GET
    @Path("/listar")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response listar() {
        return pedidoBO.listar();
    }
}
