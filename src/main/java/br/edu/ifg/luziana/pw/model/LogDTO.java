package br.edu.ifg.luziana.pw.model;

public class LogDTO {

    private String acao;
    private String detalhes;
    private String usuario;
    private String email;
    private String origem;

    public LogDTO() {
    }

    public LogDTO(String acao, String detalhes) {
        this.acao = acao;
        this.detalhes = detalhes;
        this.origem = "web";
    }

    public LogDTO(String acao, String detalhes, String usuario, String email, String origem) {
        this.acao = acao;
        this.detalhes = detalhes;
        this.usuario = usuario;
        this.email = email;
        this.origem = origem;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }
}
