package br.edu.ifg.luziana.pw;

import br.edu.ifg.luziana.pw.model.entity.Usuario;
import br.edu.ifg.luziana.pw.model.entity.Veiculo;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

/**
 * Carrega usuários e veículos de demonstração na primeira subida (tabela usuario vazia).
 *
 * Substitui o import.sql: com schema-management=update o Hibernate NÃO executa o import.sql,
 * então o banco ficava sem usuários e ninguém conseguia fazer login.
 * É idempotente: se já existir qualquer usuário, não faz nada.
 */
@ApplicationScoped
public class DadosIniciais {

    @Inject
    EntityManager em;

    @Transactional
    public void carregar(@Observes StartupEvent evento) {
        Long total = em.createQuery("select count(u) from Usuario u", Long.class).getSingleResult();
        if (total != null && total > 0) {
            return;
        }

        Usuario admin = usuario("Daniel", "Lucena", "daniel.lucena@ifg.edu.br", "(62) 99999-0001",
                "111.111.111-11", "Luziânia", "GO", "admin", "Administrador do sistema");
        Usuario maria = usuario("Maria", "Silva", "maria@automarket.com", "(11) 98888-0002",
                "222.222.222-22", "São Paulo", "SP", "vendedor", "Vendedora de veículos");
        usuario("João", "Santos", "joao@automarket.com", "(21) 97777-0003",
                "333.333.333-33", "Rio de Janeiro", "RJ", "cliente", "Cliente interessado em carros");

        veiculo("Volkswagen", "Polo", "200 TSI Comfortline", 2023, 12800, 89900.0, "automatico", "flex",
                "Preto", 116.0, "São Paulo", "SP", "Polo TSI impecável, único dono.", "(11) 99999-0001", maria);
        veiculo("Fiat", "Argo", "Drive 1.3", 2022, 28500, 62500.0, "manual", "flex",
                "Branco", 109.0, "Goiânia", "GO", "Argo Drive em ótimo estado.", "(62) 99999-0002", maria);
        veiculo("Toyota", "Corolla", "XEi 2.0", 2021, 42000, 118900.0, "automatico", "flex",
                "Prata", 177.0, "Curitiba", "PR", "Corolla XEi completo, revisões em dia.", "(41) 99999-0003", admin);
    }

    private Usuario usuario(String nome, String sobrenome, String email, String telefone, String cpf,
                            String cidade, String estado, String tipo, String bio) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setSobrenome(sobrenome);
        u.setEmail(email);
        u.setSenha("123456");
        u.setTelefone(telefone);
        u.setCpf(cpf);
        u.setCidade(cidade);
        u.setEstado(estado);
        u.setTipo(tipo);
        u.setBio(bio);
        em.persist(u);
        return u;
    }

    private void veiculo(String marca, String modelo, String versao, Integer ano, Integer km, Double preco,
                         String cambio, String combustivel, String cor, Double potencia, String cidade,
                         String estado, String descricao, String telefone, Usuario dono) {
        Veiculo v = new Veiculo();
        v.setMarca(marca);
        v.setModelo(modelo);
        v.setVersao(versao);
        v.setAno(ano);
        v.setKm(km);
        v.setPreco(preco);
        v.setCambio(cambio);
        v.setCombustivel(combustivel);
        v.setCor(cor);
        v.setPotencia(potencia);
        v.setCidade(cidade);
        v.setEstado(estado);
        v.setDescricao(descricao);
        v.setTelefone(telefone);
        v.setCriadoEm(LocalDateTime.now());
        v.setUsuario(dono);
        em.persist(v);
    }
}
