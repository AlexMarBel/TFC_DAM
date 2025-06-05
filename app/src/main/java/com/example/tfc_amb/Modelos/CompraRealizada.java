package com.example.tfc_amb.Modelos;

import java.util.Date;
import java.util.List;

public class CompraRealizada {
    private List<ProductoCarrito> productos;
    private Date fechaCompra;
    private double precio;
    private double gastoEnvio;
    private boolean compraProcesada;

    private Direccion direccion;
    private Usuario user;

    public CompraRealizada() {
    }

    public CompraRealizada(List<ProductoCarrito> productos, Date fechaCompra, double precio, double gastoEnvio, boolean compraProcesada, Direccion direccion, Usuario user) {
        this.productos = productos;
        this.fechaCompra = fechaCompra;
        this.precio = precio;
        this.gastoEnvio = gastoEnvio;
        this.compraProcesada = compraProcesada;
        this.direccion = direccion;
        this.user = user;
    }

    public List<ProductoCarrito> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoCarrito> productos) {
        this.productos = productos;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getGastoEnvio() {
        return gastoEnvio;
    }

    public void setGastoEnvio(double gastoEnvio) {
        this.gastoEnvio = gastoEnvio;
    }

    public boolean isCompraProcesada() {
        return compraProcesada;
    }

    public void setCompraProcesada(boolean compraProcesada) {
        this.compraProcesada = compraProcesada;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario userID) {
        this.user = userID;
    }

}
