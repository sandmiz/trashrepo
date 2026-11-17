package br.edu.ifg.luziana.pw.model;

public class PedidoDTO {

    private Long id;
    private Long veiculoId;
    private Double valorProposta;
    private String formaPagamento;
    private String mensagem;
    private String status; // pendente, aprovado, rejeitado

    public PedidoDTO() {
    }

    public PedidoDTO(Long veiculoId, Double valorProposta, String formaPagamento, String mensagem) {
        this.veiculoId = veiculoId;
        this.valorProposta = valorProposta;
        this.formaPagamento = formaPagamento;
        this.mensagem = mensagem;
        this.status = "pendente";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(Long veiculoId) {
        this.veiculoId = veiculoId;
    }

    public Double getValorProposta() {
        return valorProposta;
    }

    public void setValorProposta(Double valorProposta) {
        this.valorProposta = valorProposta;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
