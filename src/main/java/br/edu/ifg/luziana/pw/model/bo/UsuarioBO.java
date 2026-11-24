package br.edu.ifg.luziana.pw.model.bo;

import br.edu.ifg.luziana.pw.model.CadastroDTO;
import br.edu.ifg.luziana.pw.model.LoginDTO;
import br.edu.ifg.luziana.pw.model.dao.UsuarioDAO;
import br.edu.ifg.luziana.pw.model.entity.Usuario;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.time.ZonedDateTime;

/**
 * Regras de negócio relacionadas a usuário (login e cadastro).
 */
@RequestScoped
public class UsuarioBO {

    @Inject
    UsuarioDAO usuarioDAO;

    public boolean validarLogin(LoginDTO dto) {
        if (dto == null) {
            return false;
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            return false;
        }
        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            return false;
        }
        return dto.getEmail().contains("@") && dto.getSenha().length() >= 6;
    }

    public boolean validarCadastro(CadastroDTO dto) {
        if (dto == null) {
            return false;
        }
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            return false;
        }
        if (dto.getSobrenome() == null || dto.getSobrenome().isBlank()) {
            return false;
        }
        if (dto.getEmail() == null || !dto.getEmail().contains("@")) {
            return false;
        }
        if (dto.getSenha() == null || dto.getSenha().length() < 6) {
            return false;
        }
        return true;
    }

    public String mensagemErroLogin(LoginDTO dto) {
        if (dto == null) {
            return "Dados de login não informados.";
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            return "Informe o e-mail.";
        }
        if (!dto.getEmail().contains("@")) {
            return "E-mail inválido.";
        }
        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            return "Informe a senha.";
        }
        if (dto.getSenha().length() < 6) {
            return "A senha deve ter no mínimo 6 caracteres.";
        }
        return null;
    }

    public String mensagemErroCadastro(CadastroDTO dto) {
        if (dto == null) {
            return "Dados de cadastro não informados.";
        }
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            return "Informe o nome.";
        }
        if (dto.getSobrenome() == null || dto.getSobrenome().isBlank()) {
            return "Informe o sobrenome.";
        }
        if (dto.getEmail() == null || !dto.getEmail().contains("@")) {
            return "E-mail inválido.";
        }
        if (dto.getSenha() == null || dto.getSenha().length() < 6) {
            return "A senha deve ter no mínimo 6 caracteres.";
        }
        return null;
    }

    @Transactional
    public Response autenticar(LoginDTO dto) {
        String erro = mensagemErroLogin(dto);
        if (erro != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(erro).build();
        }
        Usuario u = usuarioDAO.autenticar(dto.getEmail(), dto.getSenha());
        if (u == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("E-mail ou senha inválidos.").build();
        }

        String token = Jwt
                .issuer("http://localhost/issuer")
                .upn(u.getEmail())
                .groups(u.getTipo())
                .claim("nome", u.getNome())
                .claim("data", ZonedDateTime.now().toString())
                .sign();

        NewCookie cookie = new NewCookie.Builder("jwt")
                .value(token)
                .path("/")
                .maxAge(3600)
                .secure(false)
                .httpOnly(true)
                .build();

        return Response.ok(u).header("Set-Cookie", cookie).build();
    }

    @Transactional
    public Response cadastrar(CadastroDTO dto) {
        String erro = mensagemErroCadastro(dto);
        if (erro != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(erro).build();
        }
        if (usuarioDAO.findByEmail(dto.getEmail()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Já existe um usuário com este e-mail.").build();
        }
        Usuario entity = new Usuario();
        entity.setNome(dto.getNome());
        entity.setSobrenome(dto.getSobrenome());
        entity.setEmail(dto.getEmail());
        entity.setSenha(dto.getSenha());
        entity.setTelefone(dto.getTelefone());
        String tipo = dto.getTipo() != null ? dto.getTipo().trim().toLowerCase() : "cliente";
        if (!tipo.equals("cliente") && !tipo.equals("vendedor") && !tipo.equals("admin")) {
            tipo = "cliente";
        }
        entity.setTipo(tipo);
        usuarioDAO.insert(entity);

        String token = Jwt
                .issuer("http://localhost/issuer")
                .upn(entity.getEmail())
                .groups(entity.getTipo())
                .claim("nome", entity.getNome())
                .claim("data", ZonedDateTime.now().toString())
                .sign();

        NewCookie cookie = new NewCookie.Builder("jwt")
                .value(token)
                .path("/")
                .maxAge(3600)
                .secure(false)
                .httpOnly(true)
                .build();

        return Response.status(Response.Status.CREATED).entity(entity).header("Set-Cookie", cookie).build();
    }
}
