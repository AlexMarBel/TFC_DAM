package com.example.tfc_amb.Modelos;

import java.util.List;

public class Categorias {
    private String titulo;
    private int id;
    private String urlFoto;

    public Categorias() {
    }

    public Categorias(String titulo, int id, String urlFoto, List<Producto> productos) {
        this.titulo = titulo;
        this.id = id;
        this.urlFoto = urlFoto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

}
