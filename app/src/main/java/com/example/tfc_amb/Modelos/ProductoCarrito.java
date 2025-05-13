package com.example.tfc_amb.Modelos;

import java.io.Serializable;

/** Implementamos serializable para mas tarde poder pasar una lista de ProductoCarrito
 * a traves de un item. La clase CompraRealizada utiliza la lista ProductoCarrito.
 */
public class ProductoCarrito implements Serializable {
    private int id;
    private String titulo;
    private int cantidadComprada;
    private double precio;

    public ProductoCarrito () {
    }

    public ProductoCarrito(int id, String titulo, int cantidadComprada, double precio) {
        this.id = id;
        this.titulo = titulo;
        this.cantidadComprada = cantidadComprada;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getCantidadComprada() {
        return cantidadComprada;
    }

    public void setCantidadComprada(int cantidadComprada) {
        this.cantidadComprada = cantidadComprada;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
