package com.example.topitzin.simu.objetos;

/**
 * Created by topitzin on 27/04/2017.
 */

public class Bitacora {

    private String descripcion, evento, fecha;

    public Bitacora() {
    }

    public Bitacora(String descripcion, String evento) {
        this.descripcion = descripcion;
        this.evento = evento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEvento() {
        return evento;
    }

    public String getFecha() {
        return fecha;
    }
}
