package br.edu.ifg.luziana.pw.model.bo;

import br.edu.ifg.luziana.pw.model.PedidoDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Teste unitário puro (não sobe o Quarkus nem precisa do PostgreSQL). */
class PedidoBOTest {

    private final PedidoBO bo = new PedidoBO();

    @Test
    void pedidoValidoNaoGeraErro() {
        PedidoDTO dto = new PedidoDTO(1L, 85000.0, "financiamento", "Tenho interesse");
        assertTrue(bo.validarEnvio(dto));
        assertNull(bo.mensagemErroEnvio(dto));
    }

    @Test
    void valorAbaixoDoMinimoEhRejeitado() {
        PedidoDTO dto = new PedidoDTO(1L, 500.0, "avista", "");
        assertFalse(bo.validarEnvio(dto));
        assertEquals("Valor da proposta deve ser de no mínimo R$ 1.000.", bo.mensagemErroEnvio(dto));
    }

    @Test
    void statusInvalidoNaoPodeSerAtualizado() {
        PedidoDTO dto = new PedidoDTO();
        dto.setStatus("cancelado");
        assertFalse(bo.podeAtualizarStatus(dto));
        dto.setStatus("APROVADO");
        assertTrue(bo.podeAtualizarStatus(dto));
    }
}
