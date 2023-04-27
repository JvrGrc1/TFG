package com.example.tfg.entidad;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class Prenda implements Serializable {

    private String nombre;
    private List<String> tallas;
    private double precio;
    private List<String> imagen;

    public Prenda(String nombre, List<String> tallas, double precio, List<String> urlImagen){
        this.nombre = nombre;
        this.tallas = tallas;
        this.precio = precio;
        this.imagen = urlImagen;
    }

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public List<String> getTallas() {return tallas;}
    public void setTallas(List<String> tallas) {this.tallas = tallas;}

    public double getPrecio() {return precio;}
    public void setPrecio(double precio) {this.precio = precio;}

    public List<String> getImagen() {return imagen;}
    public void setImagen(List<String> imagen) {this.imagen = imagen;}

    @NonNull
    @Override
    public String toString() {
            return String.format("Prenda: %s, %f. Tallas: %s", getNombre(), getPrecio(), getTallas());
    }
}
