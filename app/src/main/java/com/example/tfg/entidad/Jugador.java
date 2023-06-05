package com.example.tfg.entidad;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class Jugador implements Serializable {

    private String nombre;
    private String apellido1;
    private String apellido2;
    private String manoDominante;
    private List<Temporada> temporadas;

    public Jugador(String nombre, String apellido1, String apellido2, String manoDominante, List<Temporada> temporadas){
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.manoDominante = manoDominante;
        this.temporadas = temporadas;
    }

    public String getNombre() {return nombre;}

    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getApellido1() {return apellido1;}

    public void setApellido1(String apellido1) {this.apellido1 = apellido1;}

    public String getApellido2() {return apellido2;}

    public void setApellido2(String apellido2) {this.apellido2 = apellido2;}

    public String getManoDominante() {return manoDominante;}

    public void setManoDominante(String manoDominante) {this.manoDominante = manoDominante;}

    public List<Temporada> getTemporadas() {return temporadas;}

    public void setTemporadas(List<Temporada> temporadas) {this.temporadas = temporadas;}

    @NonNull
    @Override
    public String toString() {
        return String.format("Nombre: %s, 1Apellido: %s, 2Apellido: %s, Mano dominante: %s, Temporadas: %s", getNombre(), getApellido1(), getApellido2(), getManoDominante(), getTemporadas());
    }
}
