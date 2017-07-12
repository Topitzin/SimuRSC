package com.example.topitzin.simu.objetos;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by topitzin on 04/04/2017.
 */

public class NotificacionIdTokenService extends FirebaseInstanceIdService {

    private static final String TAG = "Firebase_Token";
    FirebaseAuth Auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String correo;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        enviarTokenRegistro(refreshedToken);
    }

    private void enviarTokenRegistro(String refreshedToken) {
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        editor.putString("Token", refreshedToken);
        editor.apply();
    }
}
