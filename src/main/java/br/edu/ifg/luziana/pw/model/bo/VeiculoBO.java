package br.edu.ifg.luziana.pw.model.bo;

import br.edu.ifg.luziana.pw.model.VeiculoDTO;
import br.edu.ifg.luziana.pw.model.dao.UsuarioDAO;
import br.edu.ifg.luziana.pw.model.dao.VeiculoDAO;
import br.edu.ifg.luziana.pw.model.entity.Usuario;
import br.edu.ifg.luziana.pw.model.entity.Veiculo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonNumber;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class VeiculoBO {
    @Inject
    JsonWebToken jwt;

    @Inject
    VeiculoDAO veiculoDAO;

    @Inject
    UsuarioDAO usuarioDAO;

    public boolean validarCadastro(VeiculoDTO dto) {
        if (dto == null) return false;
        if (dto.getMarca() == null || dto.getMarca().isBlank()) return false;
        if (dto.getModelo() == null || dto.getModelo().isBlank()) return false;
        if (dto.getAno() == null || dto.getAno() < 1950) return false;
        Double preco = dto.getPreco() != null ? dto.getPreco() : dto.getValor();
        if (preco == null || preco <= 0) return false;
        return true;
    }

    public String mensagemErroCadastro(VeiculoDTO dto) {
        if (dto == null) return "Dados do veículo não informados.";
        if (dto.getMarca() == null || dto.getMarca().isBlank()) return "Informe a marca.";
        if (dto.getModelo() == null || dto.getModelo().isBlank()) return "Informe o modelo.";
        if (dto.getAno() == null || dto.getAno() < 1950) return "Ano inválido.";
        Double preco = dto.getPreco() != null ? dto.getPreco() : dto.getValor();
        if (preco == null || preco <= 0) return "Informe um preço válido.";
        return null;
    }

    public boolean temFiltros(VeiculoDTO dto) {
        if (dto == null) return false;
        return (dto.getMarca() != null && !dto.getMarca().isBlank())
                || (dto.getModelo() != null && !dto.getModelo().isBlank())
                || dto.getAno() != null
                || (dto.getCor() != null && !dto.getCor().isBlank())
                || (dto.getPreco() != null || dto.getValor() != null);
    }

    @Transactional
    public Response salvar(VeiculoDTO dto) {
        String erro = mensagemErroCadastro(dto);
        if (erro != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(erro).build();
        }
        Veiculo entity = new Veiculo();
        entity.setMarca(dto.getMarca());
        entity.setModelo(dto.getModelo());
        entity.setVersao(dto.getVersao());
        entity.setAno(dto.getAno());
        entity.setKm(dto.getKm());
        entity.setPreco(dto.getPreco() != null ? dto.getPreco() : dto.getValor());
        entity.setCambio(dto.getCambio());
        entity.setCombustivel(dto.getCombustivel());
        entity.setCor(dto.getCor());
        entity.setPotencia(dto.getPotencia());
        entity.setCidade(dto.getCidade());
        entity.setEstado(dto.getEstado());
        entity.setDescricao(dto.getDescricao());
        entity.setTelefone(dto.getTelefone());
        entity.setCriadoEm(LocalDateTime.now());
        Usuario u = usuarioDAO.findById(((JsonNumber) jwt.getClaim("id")).intValue());
        entity.setUsuario(u);
        veiculoDAO.insert(entity);
        return Response.status(Response.Status.CREATED).entity(entity).build();
    }

    public Response listar() {
        List<Veiculo> lista = veiculoDAO.listarTodos();
        return Response.ok(lista).build();
    }

    public Response buscar(String marca, String modelo, Integer anoMin, Integer anoMax,
                           Double precoMin, Double precoMax, String cidade) {
        List<Veiculo> lista = veiculoDAO.buscar(marca, modelo, anoMin, anoMax, precoMin, precoMax, cidade);
        return Response.ok(lista).build();
    }
}
