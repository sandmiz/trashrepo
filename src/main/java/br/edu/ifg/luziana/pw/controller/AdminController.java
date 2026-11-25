package br.edu.ifg.luziana.pw.controller;

import br.edu.ifg.luziana.pw.model.LogDTO;
import br.edu.ifg.luziana.pw.model.PedidoDTO;
import br.edu.ifg.luziana.pw.model.bo.LogBO;
import br.edu.ifg.luziana.pw.model.bo.PedidoBO;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/admin-pedidos")
public class AdminController {

    @Inject
    PedidoBO pedidoBO;

    @Inject
    LogBO logBO;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance admin();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({ "admin" })
    public TemplateInstance admin() {
        return Templates.admin();
    }

    @POST
    @Path("/atualizar-status")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin" })
    public Response atualizarStatus(PedidoDTO dto,
                                    @QueryParam("adminNome") String adminNome,
                                    @QueryParam("adminEmail") String adminEmail,
                                    @QueryParam("motivo") String motivo) {
        if (dto == null || dto.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Informe o id do pedido."))
                    .build();
        }
        if (!pedidoBO.podeAtualizarStatus(dto)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Status inválido. Use: pendente, aprovado ou rejeitado."))
                    .build();
        }
        Response resp = pedidoBO.atualizarStatus(dto.getId().intValue(), dto.getStatus());
        if (resp.getStatus() == 200) {
            try {
                LogDTO log = new LogDTO("ATUALIZAR_STATUS",
                        "Pedido id=" + dto.getId() + " status=" + dto.getStatus()
                                + (motivo != null && !motivo.isBlank() ? " — motivo: " + motivo : ""),
                        adminNome != null ? adminNome : "Admin",
                        adminEmail,
                        "web");
                logBO.registrar(log);
            } catch (Exception ignored) {
            }
        }
        return resp;
    }

    @GET
    @Path("/listar")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "admin" })
    public Response listar() {
        return pedidoBO.listar();
    }
}
