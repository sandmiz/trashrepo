package br.edu.ifg.luziana.pw.model.dao;

import br.edu.ifg.luziana.pw.model.entity.LogAuditoria;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@RequestScoped
public class LogAuditoriaDAO {

    @Inject
    EntityManager entityManager;

    public void insert(LogAuditoria entity) {
        entityManager.persist(entity);
    }

    public LogAuditoria findById(Integer id) {
        return entityManager.find(LogAuditoria.class, id);
    }

    public List<LogAuditoria> listarTodos() {
        return entityManager.createQuery(
                "select l from LogAuditoria l order by l.data desc", LogAuditoria.class)
                .getResultList();
    }

    public List<LogAuditoria> listarRecentes(int limite) {
        return entityManager.createQuery(
                "select l from LogAuditoria l order by l.data desc", LogAuditoria.class)
                .setMaxResults(limite)
                .getResultList();
    }

    public List<LogAuditoria> listarPorEmail(String email) {
        return entityManager.createQuery(
                "select l from LogAuditoria l where l.email = :email order by l.data desc",
                LogAuditoria.class)
                .setParameter("email", email)
                .getResultList();
    }
}
