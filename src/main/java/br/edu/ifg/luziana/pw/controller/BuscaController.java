package br.edu.ifg.luziana.pw.controller;

import br.edu.ifg.luziana.pw.model.VeiculoDTO;
import br.edu.ifg.luziana.pw.model.bo.VeiculoBO;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class BuscaController {

    @Inject
    VeiculoBO veiculoBO;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance busca();
    }

    @GET
    @Path("/busca")
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public TemplateInstance busca() {
        return Templates.busca();
    }

    @POST
    @Path("/buscar")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response buscar(VeiculoDTO dto) {
        String marca = dto != null ? dto.getMarca() : null;
        String modelo = dto != null ? dto.getModelo() : null;
        Integer ano = dto != null ? dto.getAno() : null;
        Double preco = dto != null ? (dto.getPreco() != null ? dto.getPreco() : dto.getValor()) : null;
        String cidade = dto != null ? dto.getCidade() : null;
        return veiculoBO.buscar(marca, modelo, ano, ano, null, preco, cidade);
    }

    @GET
    @Path("/api/veiculos")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response listarVeiculos(
            @QueryParam("marca") String marca,
            @QueryParam("modelo") String modelo,
            @QueryParam("anoMin") Integer anoMin,
            @QueryParam("anoMax") Integer anoMax,
            @QueryParam("precoMin") Double precoMin,
            @QueryParam("precoMax") Double precoMax,
            @QueryParam("cidade") String cidade) {
        if (marca != null || modelo != null || anoMin != null || anoMax != null
                || precoMin != null || precoMax != null || cidade != null) {
            return veiculoBO.buscar(marca, modelo, anoMin, anoMax, precoMin, precoMax, cidade);
        }
        return veiculoBO.listar();
    }
}
