package br.edu.ifg.luziana.pw.model.dao;

import br.edu.ifg.luziana.pw.model.entity.Veiculo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@RequestScoped
public class VeiculoDAO {

    @Inject
    EntityManager entityManager;

    public void insert(Veiculo entity) {
        entityManager.persist(entity);
    }

    public void update(Veiculo entity) {
        entityManager.merge(entity);
    }

    public void delete(Veiculo entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    public Veiculo findById(Integer id) {
        return entityManager.find(Veiculo.class, id);
    }

    public List<Veiculo> listarTodos() {
        return entityManager.createQuery(
                "select v from Veiculo v order by v.id", Veiculo.class)
                .getResultList();
    }

    public List<Veiculo> buscar(String marca, String modelo, Integer anoMin, Integer anoMax,
                                Double precoMin, Double precoMax, String cidade) {
        StringBuilder jpql = new StringBuilder("select v from Veiculo v where 1=1");

        if (marca != null && !marca.isBlank()) {
            jpql.append(" and lower(v.marca) like lower(concat('%', :marca, '%'))");
        }
        if (modelo != null && !modelo.isBlank()) {
            jpql.append(" and lower(v.modelo) like lower(concat('%', :modelo, '%'))");
        }
        if (anoMin != null) {
            jpql.append(" and v.ano >= :anoMin");
        }
        if (anoMax != null) {
            jpql.append(" and v.ano <= :anoMax");
        }
        if (precoMin != null) {
            jpql.append(" and v.preco >= :precoMin");
        }
        if (precoMax != null) {
            jpql.append(" and v.preco <= :precoMax");
        }
        if (cidade != null && !cidade.isBlank()) {
            jpql.append(" and lower(v.cidade) like lower(concat('%', :cidade, '%'))");
        }
        jpql.append(" order by v.id");

        var query = entityManager.createQuery(jpql.toString(), Veiculo.class);

        if (marca != null && !marca.isBlank()) {
            query.setParameter("marca", marca);
        }
        if (modelo != null && !modelo.isBlank()) {
            query.setParameter("modelo", modelo);
        }
        if (anoMin != null) {
            query.setParameter("anoMin", anoMin);
        }
        if (anoMax != null) {
            query.setParameter("anoMax", anoMax);
        }
        if (precoMin != null) {
            query.setParameter("precoMin", precoMin);
        }
        if (precoMax != null) {
            query.setParameter("precoMax", precoMax);
        }
        if (cidade != null && !cidade.isBlank()) {
            query.setParameter("cidade", cidade);
        }

        return query.getResultList();
    }

    public List<Veiculo> listarPorUsuario(Integer usuarioId) {
        return entityManager.createQuery(
                "select v from Veiculo v where v.usuario.id = :uid order by v.id",
                Veiculo.class)
                .setParameter("uid", usuarioId)
                .getResultList();
    }
}
