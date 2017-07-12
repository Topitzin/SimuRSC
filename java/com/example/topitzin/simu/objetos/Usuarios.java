package com.example.topitzin.simu.objetos;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by topitzin on 04/04/2017.
 */

public class Usuarios {
    String email, name, token, llave;
    Boolean enablePush, enableEmail, administrador;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnablePush() {
        return enablePush;
    }

    public void setEnablePush(Boolean enablePush) {
        this.enablePush = enablePush;
    }

    public Boolean getEnableEmail() {
        return enableEmail;
    }

    public void setEnableEmail(Boolean enableEmail) {
        this.enableEmail = enableEmail;
    }

    public Boolean getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Boolean administrador) {
        this.administrador = administrador;
    }

    public Usuarios() {
    }

    public Usuarios(String email, String name , String token, Boolean push, Boolean eEmail, Boolean admin) {
        this.email = email;
        this.name = name;
        this.enablePush = push;
        this.token = token;
        this.enableEmail = eEmail;
        this.administrador = admin;
    }

    public String getLlave() {
        return llave;
    }

    public void setLlave(String llave) {
        this.llave = llave;
    }

    public Usuarios(String correo, String nombre, String token) {
        this.email = correo;
        this.name = nombre;
        this.token = token;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return name;
    }

    public void setNombre(String nombre) {
        this.name = nombre;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("name", name);
        result.put("token", token);
        result.put("enablePush", enablePush);
        result.put("enableEmail", enableEmail);
        result.put("administrador", administrador);

        return result;
    }
}
