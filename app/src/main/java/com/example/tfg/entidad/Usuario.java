package com.example.tfg.entidad;


import androidx.annotation.NonNull;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String correo, nombre, apellido1, apellido2, rol, imagen, tlf, direccion;
    public Usuario(){}

    public Usuario(String correo, String nombre, String apellido1, String rol) {
        this.correo = correo;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.rol = rol;
        this.imagen = null;
        this.apellido2 = null;
        this.tlf = null;
        this.direccion = null;
    }

    public Usuario(String correo, String nombre, String apellido1, String apellido2, String rol, String imagen, String tlf, String direccion) {
        this.correo = correo;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.rol = rol;
        this.imagen = imagen;
        this.apellido2 = apellido2;
        this.tlf = tlf;
        this.direccion = direccion;
    }

    public String getCorreo() {return correo;}
    public void setCorreo(String correo) {this.correo = correo;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getApellido1() {return apellido1;}
    public void setApellido1(String apellido1) {this.apellido1 = apellido1;}

    public String getApellido2() {return apellido2;}
    public void setApellido2(String apellido2) {this.apellido2 = apellido2;}

    public String getRol() {return rol;}
    public void setRol(String rol) {this.rol = rol;}

    public String getImagen() {return imagen;}
    public void setImagen(String imagen) {this.imagen = imagen;}

    public String getTlf() {return tlf;}
    public void setTlf(String tlf) {this.tlf = tlf;}

    public String getDireccion() {return direccion;}
    public void setDireccion(String direccion) {this.direccion = direccion;}

    @NonNull
    @Override
    public String toString() {
        return String.format("Usuario[ Nombre: %s, Correo: %s, Rol: %s]", getNombre(), getCorreo(), getRol());
    }
}
