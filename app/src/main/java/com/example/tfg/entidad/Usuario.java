package com.example.tfg.entidad;


import androidx.annotation.NonNull;

public class Usuario {

    private String correo;
    private String nombre;
    private String rol;

    //FOTO DE PERFIL NO SE SI IRIA AQUI Y FALTARIAN DATOS BASICOS: EDAD, DIRECCION, ETC

    public Usuario(String correo, String nombre, String rol) {
        this.correo = correo;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("Usuario[ Nombre: %s, Correo: %s, Rol: %s]", getNombre(), getCorreo(), getRol());
    }
}
