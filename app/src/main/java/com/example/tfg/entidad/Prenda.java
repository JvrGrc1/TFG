package com.example.tfg.entidad;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class Prenda implements Serializable {

    private String nombre;
    private List<String> tallas;
    private String temporada;
    private double precio;
    private String imagen;

    public Prenda(String nombre, List<String> tallas, String temporada, double precio, String urlImagen){
        this.nombre = nombre;
        this.tallas = tallas;
        this.precio = precio;
        this.temporada = temporada;
        this.imagen = urlImagen;
    }

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public List<String> getTallas() {return tallas;}
    public void setTallas(List<String> tallas) {this.tallas = tallas;}

    public String getTemporada() {return temporada;}
    public void setTemporada(String temporada) {this.temporada = temporada;}

    public double getPrecio() {return precio;}
    public void setPrecio(double precio) {this.precio = precio;}

    public String getImagen() {return imagen;}
    public void setImagen(String imagen) {this.imagen = imagen;}

    @NonNull
    @Override
    public String toString() {
        if (getTemporada() != null) {
            return String.format("Prenda: %s, %s, %f. Tallas: %s", getNombre(), getTemporada(), getPrecio(), getTallas());
        }else{
            return String.format("Prenda: %s, %f. Tallas: %s", getNombre(), getPrecio(), getTallas());
        }
    }
}
