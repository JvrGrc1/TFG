package com.example.tfg.entidad;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Jornada implements Serializable {

    private long jornada;
    private List<Partido> partidos = new ArrayList<>();

    public Jornada(long jornada, List<Partido> partidos) {
        this.jornada = jornada;
        this.partidos = partidos;
    }

    public Jornada(long jornada, Partido partidos) {
        this.jornada = jornada;
        this.partidos.add(partidos);
    }

    public long getJornada() {
        return jornada;
    }

    public void setJornada(long jornada) {
        this.jornada = jornada;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public void setPartidos(List<Partido> partidos) {
        this.partidos = partidos;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("Jornada %d. Partidos: %s", getJornada(), getPartidos());
    }
}
