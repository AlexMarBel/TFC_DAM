package com.example.tfc_amb.Modelos;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String id;
    private String nombre;
    private String email;
    private String apellidos;
    private String telefono;
    private boolean admin;

    public Usuario () {
    }

    public Usuario(String id, String nombre, String email, String apellidos, String telefono, boolean admin) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.admin = admin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
