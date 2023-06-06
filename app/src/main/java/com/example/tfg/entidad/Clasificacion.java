package com.example.tfg.entidad;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Clasificacion implements Serializable {

    private String nombre;
    private String temporada;
    private int victoria;
    private int empate;
    private int derrota;
    private int puntos;

    public Clasificacion(String nombre, String temporada){
        this.nombre = nombre;
        this.temporada = temporada;
        this.victoria = 0;
        this.empate = 0;
        this.derrota = 0;
        this.puntos = 0;
    }

    public Clasificacion(String nombre, String temporada, int victoria, int empate, int derrota){
        this.nombre = nombre;
        this.temporada = temporada;
        this.victoria = victoria;
        this.empate = empate;
        this.derrota = derrota;
        calcular();
    }

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getTemporada() {return temporada;}
    public void setTemporada(String temporada) {this.temporada = temporada;}

    public int getVictoria() {return victoria;}
    public void setVictoria(int victoria) {this.victoria = victoria;}

    public int getEmpate() {return empate;}
    public void setEmpate(int empate) {this.empate = empate;}

    public int getDerrota() {return derrota;}
    public void setDerrota(int derrota) {this.derrota = derrota;}

    public int getPuntos() {return puntos;}
    public void setPuntos(int puntos) {this.puntos = puntos;}

    private void calcular(){
        this.puntos = ((this.victoria * 2) + this.empate);      //En balonmano la victoria vale 2 puntos, el empate 1 punto y la errota 0 puntos.

    }

    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("Equipo: %s, Temporada: %s [Puntos: %d, Victorias: %d, Empates: %d, Derrotas: %d]", getNombre(), getTemporada(), getPuntos(), getVictoria(), getEmpate(), getDerrota());
    }
}
