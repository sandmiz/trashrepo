package br.edu.ifg.luziana.pw.controller;

import br.edu.ifg.luziana.pw.model.LogDTO;
import br.edu.ifg.luziana.pw.model.bo.LogBO;
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

@Path("/logs")
public class LogsController {

    @Inject
    LogBO logBO;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance logs();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance logs() {
        return Templates.logs();
    }

    @POST
    @Path("/registrar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrar(LogDTO dto) {
        return logBO.registrar(dto);
    }

    @GET
    @Path("/listar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listar(@QueryParam("limite") Integer limite) {
        if (limite != null && limite > 0) {
            return logBO.listarRecentes(limite);
        }
        return logBO.listar();
    }
}
