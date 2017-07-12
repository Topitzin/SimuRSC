package com.example.topitzin.simu.objetos;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by topitzin on 08/05/2017.
 */

public class SIMU extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
