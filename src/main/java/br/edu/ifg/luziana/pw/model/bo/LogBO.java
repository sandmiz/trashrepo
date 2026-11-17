package br.edu.ifg.luziana.pw.model.bo;

import br.edu.ifg.luziana.pw.model.LogDTO;
import br.edu.ifg.luziana.pw.model.dao.LogAuditoriaDAO;
import br.edu.ifg.luziana.pw.model.entity.LogAuditoria;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class LogBO {

    @Inject
    LogAuditoriaDAO logDAO;

    public boolean validarRegistro(LogDTO dto) {
        if (dto == null) return false;
        if (dto.getAcao() == null || dto.getAcao().isBlank()) return false;
        return true;
    }

    public String mensagemErroRegistro(LogDTO dto) {
        if (dto == null) return "Dados do log não informados.";
        if (dto.getAcao() == null || dto.getAcao().isBlank()) return "Informe a ação do log.";
        return null;
    }

    public void aplicarPadroes(LogDTO dto) {
        if (dto == null) return;
        if (dto.getOrigem() == null || dto.getOrigem().isBlank()) dto.setOrigem("web");
        if (dto.getDetalhes() == null) dto.setDetalhes("");
    }

    @Transactional
    public Response registrar(LogDTO dto) {
        String erro = mensagemErroRegistro(dto);
        if (erro != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(erro).build();
        }
        aplicarPadroes(dto);
        LogAuditoria entity = new LogAuditoria();
        entity.setData(LocalDateTime.now());
        entity.setUsuario(dto.getUsuario());
        entity.setEmail(dto.getEmail());
        entity.setAcao(dto.getAcao());
        entity.setDetalhes(dto.getDetalhes());
        entity.setOrigem(dto.getOrigem());
        logDAO.insert(entity);
        return Response.status(Response.Status.CREATED).entity(entity).build();
    }

    public Response listar() {
        List<LogAuditoria> lista = logDAO.listarTodos();
        return Response.ok(lista).build();
    }

    public Response listarRecentes(int limite) {
        List<LogAuditoria> lista = logDAO.listarRecentes(limite);
        return Response.ok(lista).build();
    }
}
