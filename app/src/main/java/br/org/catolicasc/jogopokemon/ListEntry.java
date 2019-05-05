package br.org.catolicasc.jogopokemon;

public class ListEntry {
    private String id;
    private String nome;
    private String nomeRespondido;
    private String imgUrl;

    public ListEntry(String id, String nome, String imgUrl) {
        this.id = id;
        this.nome = nome;
        this.imgUrl = imgUrl;
    }

    public void setNomeRespondido(String nomeRespondido) {
        this.nomeRespondido = nomeRespondido;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getNomeRespondido() {
        return nomeRespondido;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
