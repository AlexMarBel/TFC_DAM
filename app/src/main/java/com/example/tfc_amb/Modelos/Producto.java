package com.example.tfc_amb.Modelos;

public class Producto {
    private int id, cantidad, cantidadVendida;
    private String titulo, urlFoto, categoria;

    private double precio;

    public Producto() {

    }

    public Producto(int id, int cantidad, int cantidadVendida, String titulo, String urlFoto, String categoria, double precio) {
        this.id = id;
        this.cantidad = cantidad;
        this.cantidadVendida = cantidadVendida;
        this.titulo = titulo;
        this.urlFoto = urlFoto;
        this.categoria = categoria;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String nombre) {
        this.titulo = nombre;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", cantidadVendida=" + cantidadVendida +
                ", titulo='" + titulo + '\'' +
                ", urlFoto='" + urlFoto + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                '}';
    }
}
