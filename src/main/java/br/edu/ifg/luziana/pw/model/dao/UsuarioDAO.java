package br.edu.ifg.luziana.pw.model.dao;

import br.edu.ifg.luziana.pw.model.entity.Usuario;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

@RequestScoped
public class UsuarioDAO {

    @Inject
    EntityManager entityManager;

    public void insert(Usuario entity) {
        entityManager.persist(entity);
    }

    public void update(Usuario entity) {
        entityManager.merge(entity);
    }

    public void delete(Usuario entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    public Usuario findById(Integer id) {
        return entityManager.find(Usuario.class, id);
    }

    public Usuario findByEmail(String email) {
        try {
            return entityManager.createQuery(
                            "select u from Usuario u where u.email = :email", Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Usuario autenticar(String email, String senha) {
        try {
            return entityManager.createQuery(
                            "select u from Usuario u where u.email = :email and u.senha = :senha",
                            Usuario.class)
                    .setParameter("email", email)
                    .setParameter("senha", senha)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Usuario> listarTodos() {
        return entityManager.createQuery(
                "select u from Usuario u order by u.id", Usuario.class)
                .getResultList();
    }
}
