package com.example.topitzin.simu.objetos;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by topitzin on 17/03/2017.
 */
public class Radios {
    String nombre;
    String ip;
    String llave;
    String mac;
    int tx, rx, intensidad, tiempo;
    double signal;
    Boolean estado;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }



    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }



    public Radios() {
    }



    public Radios(String nombre, String ip, int tx, int rx, double signal, Boolean estado, int intensidad, String Mac, int tiempo) {
        this.nombre = nombre;
        this.ip = ip;
        this.tx = tx;
        this.rx = rx;
        this.signal = signal;
        this.estado = estado;
        this.intensidad = intensidad;
        this.mac = Mac;
        this.tiempo = tiempo;
    }

    public int getIntensidad() {
        return intensidad;
    }

    public void setIntensidad(int intensidad) {
        this.intensidad = intensidad;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getLlave() {
        return llave;
    }

    public void setLlave(String llave) {
        this.llave = llave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getTx() {
        return tx;
    }

    public void setTx(int tx) {
        this.tx = tx;
    }

    public int getRx() {
        return rx;
    }

    public void setRx(int rx) {
        this.rx = rx;
    }

    public double getSignal() {
        return signal;
    }



    public void setSignal(double signal) {
        this.signal = signal;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ip", ip);
        result.put("nombre", nombre);
        result.put("estado", estado);
        result.put("intensidad", intensidad);
        result.put("mac", mac);
        result.put("rx", rx);
        result.put("tx", tx);
        result.put("signal", signal);

        return result;
    }
}
