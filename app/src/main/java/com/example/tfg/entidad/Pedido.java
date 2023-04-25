package com.example.tfg.entidad;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Pedido implements Serializable {

    private String prenda, talla;
    private Long cantidad;
    private double precioUnidad;
    private boolean pagado;

    public Pedido(){/*Constructor vacío necesario*/}

    public Pedido(String nombre, String talla, Long cantidad, double precioUnitario){
        this.prenda = nombre;
        this.talla = talla;
        this.cantidad = cantidad;
        this.precioUnidad = precioUnitario;
        this.pagado = false;
    }

    public String getPrenda() {return prenda;}
    public void setPrenda(String prenda) {this.prenda = prenda;}

    public String getTalla() {return talla;}
    public void setTalla(String talla) {this.talla = talla;}

    public Long getCantidad() {return cantidad;}
    public void setCantidad(Long cantidad) {this.cantidad = cantidad;}
    public double getPrecioUnidad() {return precioUnidad;}
    public void setPrecioUnidad(double precioUnidad) {this.precioUnidad = precioUnidad;}

    public boolean isPagado() {return pagado;}
    public void setPagado(boolean pagado) {this.pagado = pagado;}

    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("Pedido [Prenda: %s, Talla: %s, Cantidad: %d, Precio por unidad: %f] Pagado: %b", getPrenda(), getTalla() ,getCantidad(), getPrecioUnidad(), isPagado());
    }
}
