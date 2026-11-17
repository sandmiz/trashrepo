package br.edu.ifg.luziana.pw.model;

public class VeiculoDTO {

    private String marca;
    private String modelo;
    private String versao;
    private Integer ano;
    private Integer km;
    private Double preco;
    private String cambio;
    private String combustivel;
    private String cor;
    private Double potencia;
    private String cidade;
    private String estado;
    private String descricao;
    private String telefone;

    // Campos legados / busca (compatibilidade)
    private String tipo;
    private String quilometrosRodados;
    private Double valor;

    public VeiculoDTO() {
    }

    public VeiculoDTO(String marca, String modelo, String versao, Integer ano, Integer km,
                      Double preco, String cambio, String combustivel, String cor,
                      Double potencia, String cidade, String estado, String descricao, String telefone) {
        this.marca = marca;
        this.modelo = modelo;
        this.versao = versao;
        this.ano = ano;
        this.km = km;
        this.preco = preco;
        this.cambio = cambio;
        this.combustivel = combustivel;
        this.cor = cor;
        this.potencia = potencia;
        this.cidade = cidade;
        this.estado = estado;
        this.descricao = descricao;
        this.telefone = telefone;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getKm() {
        return km;
    }

    public void setKm(Integer km) {
        this.km = km;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getCambio() {
        return cambio;
    }

    public void setCambio(String cambio) {
        this.cambio = cambio;
    }

    public String getCombustivel() {
        return combustivel;
    }

    public void setCombustivel(String combustivel) {
        this.combustivel = combustivel;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public Double getPotencia() {
        return potencia;
    }

    public void setPotencia(Double potencia) {
        this.potencia = potencia;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getQuilometrosRodados() {
        return quilometrosRodados;
    }

    public void setQuilometrosRodados(String quilometrosRodados) {
        this.quilometrosRodados = quilometrosRodados;
    }

    /** Compatibilidade com typo antigo no formulário de busca */
    public String getQuilometrosRodadods() {
        return quilometrosRodados;
    }

    public void setQuilometrosRodadods(String quilometrosRodadods) {
        this.quilometrosRodados = quilometrosRodadods;
    }

    public Double getValor() {
        return valor != null ? valor : preco;
    }

    public void setValor(Double valor) {
        this.valor = valor;
        if (this.preco == null) {
            this.preco = valor;
        }
    }
}
