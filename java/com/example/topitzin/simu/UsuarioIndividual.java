package com.example.topitzin.simu;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.topitzin.simu.objetos.FirebaseReferences;
import com.example.topitzin.simu.objetos.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UsuarioIndividual extends AppCompatActivity {

    String[] usuario;
    AlertDialog.Builder alert;
    String key;
    View alertLayout;
    Bundle bundle;
    FloatingActionButton atras, modificar, eliminar;
    public FirebaseAuth mAuth;
    private FirebaseDatabase database;
    BottomNavigationView bottomNavigationView;
    private DatabaseReference mDB;
    TextView correo, nombre;
    SwitchCompat push, email, admin;
    Boolean pushValue, emailValue, adminValue;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_individual);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        mDB = database.getReference().getRoot();


        usuario = getIntent().getStringArrayExtra("userSelected");
        buscarObjetos();

        if (usuario.length > 0) {
            llenarespacios(usuario);
        }


        eventos();
    }

    private void buscarObjetos() {
        nombre = (TextView) findViewById(R.id.nombre);
        correo = (TextView) findViewById(R.id.correo);
        push = (SwitchCompat)findViewById(R.id.push);
        email = (SwitchCompat)findViewById(R.id.email);
        bottomNavigationView  = (BottomNavigationView) findViewById(R.id.navitationview);
        admin = (SwitchCompat)findViewById(R.id.admin);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();

    }

    private void eventos() {


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.ic_back:
                        finish();
                        break;
                    case R.id.ic_edit:
                        crearAlerta(getResources().getString(R.string.guardar_cambios), 1);
                        alert.show();
                        break;
                    case R.id.ic_erase:
                        crearAlerta(getResources().getString(R.string.eliminar_usr), 2);
                        alert.show();
                        break;
                }

                return false;
            }
        });

        admin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    adminValue = true;
                else
                    adminValue = false;
            }
        });

        email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    emailValue = true;
                else
                    emailValue = false;
            }
        });

        push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    pushValue = true;
                else
                    pushValue = false;
            }
        });
    }

    private void crearAlerta(String titulo, final int id) {
        alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do something awesome!
                if (id == 2) {
                    try{ mDB.child(FirebaseReferences.Usuarios_REFERENCE).child(key).removeValue(); finish(); } catch (Exception e){ }
                }
                else{
                    Usuarios user = new Usuarios(
                            usuario[1], /*Email*/
                            usuario[0], /*Nombre*/
                            usuario[6], /*Token*/
                            Boolean.valueOf(usuario[4]), /*Push*/
                            Boolean.valueOf(usuario[3]), /*Email*/
                            Boolean.valueOf(usuario[2]));/*Admin*/


                    user.setAdministrador(adminValue);
                    editor.putBoolean("administrador", adminValue);
                    editor.apply();
                    user.setEnableEmail(emailValue);
                    user.setEnablePush(pushValue);

                    Map<String, Object> postValues = user.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/"+FirebaseReferences.Usuarios_REFERENCE+"/" + key, postValues);

                    try { mDB.updateChildren(childUpdates); } catch (Exception e){ }

                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       finish();
                    }
                }, 500);
            }
        });
        alert.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //cancel;
            }
        });
    }

    private void llenarespacios(String[] usuario) {
        correo.setText(usuario[1]);
        nombre.setText(usuario[0]);
        push.setChecked(Boolean.valueOf(usuario[4]));
        pushValue = Boolean.valueOf(usuario[4]);
        email.setChecked(Boolean.valueOf(usuario[3]));
        emailValue = Boolean.valueOf(usuario[3]);
        admin.setChecked(Boolean.valueOf(usuario[2]));
        adminValue = Boolean.valueOf(usuario[2]);
        key = usuario[5];
    }
}
