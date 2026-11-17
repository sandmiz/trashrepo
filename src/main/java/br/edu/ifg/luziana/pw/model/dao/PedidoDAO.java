package br.edu.ifg.luziana.pw.model.dao;

import br.edu.ifg.luziana.pw.model.entity.Pedido;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@RequestScoped
public class PedidoDAO {

    @Inject
    EntityManager entityManager;

    public void insert(Pedido entity) {
        entityManager.persist(entity);
    }

    public void update(Pedido entity) {
        entityManager.merge(entity);
    }

    public void delete(Pedido entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    public Pedido findById(Integer id) {
        return entityManager.find(Pedido.class, id);
    }

    public List<Pedido> listarTodos() {
        return entityManager.createQuery(
                "select p from Pedido p left join fetch p.veiculo left join fetch p.usuario order by p.id desc",
                Pedido.class)
                .getResultList();
    }

    public List<Pedido> listarPorStatus(String status) {
        return entityManager.createQuery(
                "select p from Pedido p left join fetch p.veiculo left join fetch p.usuario where lower(p.status) = lower(:status) order by p.id desc",
                Pedido.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Pedido> listarPorUsuario(Integer usuarioId) {
        return entityManager.createQuery(
                "select p from Pedido p left join fetch p.veiculo left join fetch p.usuario where p.usuario.id = :uid order by p.id desc",
                Pedido.class)
                .setParameter("uid", usuarioId)
                .getResultList();
    }

    public List<Pedido> listarPorVeiculo(Integer veiculoId) {
        return entityManager.createQuery(
                "select p from Pedido p left join fetch p.veiculo left join fetch p.usuario where p.veiculo.id = :vid order by p.id desc",
                Pedido.class)
                .setParameter("vid", veiculoId)
                .getResultList();
    }

    public Pedido findByIdComRelacoes(Integer id) {
        try {
            return entityManager.createQuery(
                            "select p from Pedido p left join fetch p.veiculo left join fetch p.usuario where p.id = :id",
                            Pedido.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
