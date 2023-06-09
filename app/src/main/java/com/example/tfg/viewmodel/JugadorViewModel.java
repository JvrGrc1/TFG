package com.example.tfg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tfg.entidad.Jugador;

import java.util.ArrayList;
import java.util.List;

public class JugadorViewModel extends AndroidViewModel {

    private MutableLiveData<List<Jugador>> todosLosJugadores;

    public JugadorViewModel(@NonNull Application application) {
        super(application);
    }

    public void setTodosLosJugadores(MutableLiveData<List<Jugador>> jugadores){
        this.todosLosJugadores = (MutableLiveData<List<Jugador>>) jugadores;
    }

    public MutableLiveData<List<Jugador>> getTodosLosJugadores(){return todosLosJugadores;}

    public List<Jugador> devuelveBusqueda(String buscar){
        List<Jugador> devolver = new ArrayList<>();
        for (Jugador jugador : todosLosJugadores.getValue()){
            if (jugador.getNombre().contains(buscar)){
                devolver.add(jugador);
            }
        }
        return devolver;
    }
}
