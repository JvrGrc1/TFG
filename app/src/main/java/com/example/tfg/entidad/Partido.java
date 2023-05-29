package com.example.tfg.entidad;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Partido implements Serializable {

    private String division;
    private String local;
    private String visitante;
    private Long golesLocal;
    private Long golesVisitante;
    private Date fecha;
    private String pabellon;
    private LocalTime hora;
    private Long jornada;

    public Partido(String division, String local, String visitante, Long golesLocal, Long golesVisitante, Date fecha, String pabellon, LocalTime hora, Long jornada) {
        this.division = division;
        this.local = local;
        this.visitante = visitante;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.fecha = fecha;
        this.pabellon = pabellon;
        this.hora = hora;
        this.jornada = jornada;
    }

    public Partido(String division, String local, String visitante, Long golesLocal, Long golesVisitante, String fecha, String pabellon, String hora, Long jornada) {
        this.division = division;
        this.local = local;
        this.visitante = visitante;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        setFechaS(fecha);
        this.pabellon = pabellon;
        setHoraS(hora);
        this.jornada = jornada;
    }

    public Partido() {
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getVisitante() {
        return visitante;
    }

    public void setVisitante(String visitante) {
        this.visitante = visitante;
    }

    public Long getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(long golesLocal) {
        this.golesLocal = golesLocal;
    }

    public Long getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(long golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setFechaS(String fecha){
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            this.fecha = format.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getPabellon() {
        return pabellon;
    }

    public void setPabellon(String pabellon) {
        this.pabellon = pabellon;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setHoraS(String hora) {
        if (hora == null){
            this.hora = null;
        }else {
            DateTimeFormatter formatter;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("HH:mm");
                this.hora = LocalTime.parse(hora, formatter);
            }
        }
    }

    public Long getJornada() {return jornada;}

    public void setJornada(Long jornada) {this.jornada = jornada;}

    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("Jornada: %d, División: %s. %s  %d - %d  %s. Pabellón: %s, Fecha: %s, %s", getJornada(), getDivision(), getLocal(), getGolesLocal(), getGolesVisitante(), getVisitante(), getPabellon(), getFecha(), getHora().toString());
    }
}
