package com.example.tfg.entidad;

import java.io.Serializable;

public class Jugador implements Serializable {

    private String nombre;
    private String apellido1;
    private String apellido2;
    private String posicion;        //Seguramente CAMBIO

    public Jugador(String nombre, String apellido1, String apellido2, String posicion) {
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.posicion = posicion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    @Override
    public String toString() {
        return String.format("Jugador[Nombre:%s, 1º Apellido: %s, 2º Apellido: %s, Posición: %s]", getNombre(), getApellido1(), getApellido2(), getPosicion());
    }
}
