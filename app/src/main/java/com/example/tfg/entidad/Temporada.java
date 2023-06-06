package com.example.tfg.entidad;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Temporada implements Serializable {

    private String anio;
    private long dosMinutos;
    private long amarillas;
    private long rojas;
    private long paradas;
    private long disparos;
    private String posicion;
    private long dorsal;
    private long goles;
    private String equipo;

    public Temporada(String anio,long dosMinutos, long amarilla, long roja, long parada,long disparos, String posicion, long dorsal, long gol, String equipo){
        this.anio = anio;
        this.dosMinutos = dosMinutos;
        this.amarillas = amarilla;
        this.rojas = roja;
        this.paradas = parada;
        this.disparos = disparos;
        this.posicion = posicion;
        this.dorsal = dorsal;
        this.goles = gol;
        this.equipo = equipo;
    }

    public String getAnio(){return anio;}

    public void setAnio(String anio){this.anio = anio;}

    public long getDosMinutos() {return dosMinutos;}

    public void setDosMinutos(long dosMinutos) {this.dosMinutos = dosMinutos;}

    public long getAmarillas() {return amarillas;}

    public void setAmarillas(long amarillas) {this.amarillas = amarillas;}

    public long getRojas() {return rojas;}

    public void setRojas(long rojas) {this.rojas = rojas;}

    public long getParadas() {return paradas;}

    public void setParadas(long paradas) {this.paradas = paradas;}

    public long getDisparos() {return disparos;}

    public void setDisparos(long disparos) {this.disparos = disparos;}

    public String getPosicion() {return posicion;}

    public void setPosicion(String posicion) {this.posicion = posicion;}

    public long getDorsal() {return dorsal;}

    public void setDorsal(long dorsal) {this.dorsal = dorsal;}

    public long getGoles() {return goles;}

    public void setGoles(long goles) {this.goles = goles;}

    public String getEquipo() {return equipo;}

    public void setEquipo(String equipo) {this.equipo = equipo;}

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("Temporada: %s, Equipo: %s [Posicion: %s, Goles: %d, Disparos: %d, Dorsal: %d Amarillas: %d, Rojas: %d, 2 Minutos: %d, Paradas: %s]", getAnio(), getEquipo(), getPosicion(), getGoles(), getDisparos(), getDorsal(), getAmarillas(), getRojas(), getDosMinutos(), getParadas());
    }
}
