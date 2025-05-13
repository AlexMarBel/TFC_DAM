package com.example.tfc_amb.Modelos;

import java.io.Serializable;

public class Direccion implements Serializable {
    private String calle;
    private String portal;
    private String piso;
    private String puerta;
    private String ciudad;
    private String codigoPostal;

    public Direccion() {
    }

    public Direccion(String calle, String portal, String piso, String puerta, String ciudad, String codigoPostal) {
        this.calle = calle;
        this.portal = portal;
        this.piso = piso;
        this.puerta = puerta;
        this.ciudad = ciudad;
        this.codigoPostal = codigoPostal;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }

    public String getPiso() {
        return piso;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public String getPuerta() {
        return puerta;
    }

    public void setPuerta(String puerta) {
        this.puerta = puerta;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }
}
