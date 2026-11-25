package br.edu.ifg.luziana.pw.model.bo;

import br.edu.ifg.luziana.pw.model.PerfilDTO;
import br.edu.ifg.luziana.pw.model.dao.UsuarioDAO;
import br.edu.ifg.luziana.pw.model.entity.Usuario;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonNumber;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class PerfilBO {
    @Inject
    JsonWebToken jwt;

    @Inject
    UsuarioDAO usuarioDAO;

    public boolean validarSalvar(PerfilDTO dto) {
        if (dto == null) return false;
        if (dto.getNome() == null || dto.getNome().isBlank()) return false;
        if (dto.getEmail() == null || !dto.getEmail().contains("@")) return false;
        if (dto.getTelefone() == null || dto.getTelefone().isBlank()) return false;
        if (dto.getTipo() != null && !dto.getTipo().isBlank()) {
            String tipo = dto.getTipo().toLowerCase();
            if (!tipo.equals("cliente") && !tipo.equals("vendedor") && !tipo.equals("admin")) {
                return false;
            }
        }
        return true;
    }

    public String mensagemErroSalvar(PerfilDTO dto) {
        if (dto == null) return "Dados do perfil não informados.";
        if (dto.getNome() == null || dto.getNome().isBlank()) return "Informe o nome.";
        if (dto.getEmail() == null || !dto.getEmail().contains("@")) return "E-mail inválido.";
        if (dto.getTelefone() == null || dto.getTelefone().isBlank()) return "Informe o telefone.";
        if (dto.getTipo() != null && !dto.getTipo().isBlank()) {
            String tipo = dto.getTipo().toLowerCase();
            if (!tipo.equals("cliente") && !tipo.equals("vendedor") && !tipo.equals("admin")) {
                return "Tipo de conta inválido (use cliente, vendedor ou admin).";
            }
        }
        if (dto.getSenhaNova() != null && !dto.getSenhaNova().isBlank()) {
            if (dto.getSenhaNova().length() < 6) return "A nova senha deve ter no mínimo 6 caracteres.";
            if (dto.getSenhaAtual() == null || dto.getSenhaAtual().isBlank()) {
                return "Informe a senha atual para alterar a senha.";
            }
        }
        return null;
    }

    public boolean querTrocarSenha(PerfilDTO dto) {
        return dto != null && dto.getSenhaNova() != null && !dto.getSenhaNova().isBlank();
    }

    @Transactional
    public Response salvar(Integer usuarioId, PerfilDTO dto) {
        String erro = mensagemErroSalvar(dto);
        if (erro != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(erro).build();
        }
        Usuario entity = usuarioDAO.findById(usuarioId);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Usuário não encontrado.").build();
        }
        // valida ANTES de alterar a entidade gerenciada (senão o commit grava mesmo após erro)
        if (querTrocarSenha(dto) && !dto.getSenhaAtual().equals(entity.getSenha())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Senha atual incorreta.").build();
        }
        Usuario outro = usuarioDAO.findByEmail(dto.getEmail());
        if (outro != null && !outro.getId().equals(entity.getId())) {
            return Response.status(Response.Status.CONFLICT).entity("Já existe um usuário com este e-mail.").build();
        }
        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setTelefone(dto.getTelefone());
        entity.setCpf(dto.getCpf());
        entity.setCidade(dto.getCidade());
        entity.setEstado(dto.getEstado());
        if (dto.getTipo() != null && !dto.getTipo().isBlank()) {
            entity.setTipo(dto.getTipo().toLowerCase());
        }
        entity.setBio(dto.getBio());
        if (querTrocarSenha(dto)) {
            entity.setSenha(dto.getSenhaNova());
        }
        usuarioDAO.update(entity);
        return Response.ok(entity).build();
    }

    public Response buscar(Integer usuarioId) {
        Usuario entity = usuarioDAO.findById(usuarioId);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Usuário não encontrado.").build();
        }
        return Response.ok(entity).build();
    }

    public Response info() {
        Usuario u = usuarioDAO.findById(((JsonNumber) jwt.getClaim("id")).intValue());

        if (u == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Usuário inválido.").build();
        }

        return Response.ok(u).build();
    }
}
