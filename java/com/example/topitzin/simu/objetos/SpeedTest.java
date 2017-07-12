package com.example.topitzin.simu.objetos;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


public class SpeedTest {
    String fecha;
    double ping;
    double subida;

    public SpeedTest(String fecha, double bajada, double subida, double ping) {
        this.fecha = fecha;
        this.bajada = bajada;
        this.subida = subida;
        this.ping = ping;
    }

    public SpeedTest() {
    }
    double bajada;

    public double getBajada() {
        return bajada;
    }

    public void setBajada(double bajada) {
        this.bajada = bajada;
    }

    public double getSubida() {
        return subida;
    }

    public void setSubida(double subida) {
        this.subida = subida;
    }

    public double getPing() {
        return ping;
    }

    public void setPing(double ping) {
        this.ping = ping;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fecha", fecha);
        result.put("ping", ping);
        result.put("subida", subida);
        result.put("bajada", bajada);
        return result;
    }
}
