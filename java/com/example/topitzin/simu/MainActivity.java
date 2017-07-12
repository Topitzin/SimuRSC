package com.example.topitzin.simu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.topitzin.simu.objetos.Adaptador;
import com.example.topitzin.simu.objetos.FirebaseReferences;
import com.example.topitzin.simu.objetos.Radios;
import com.example.topitzin.simu.objetos.Usuarios;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener, NanoStationFragment.OnFragmentInteractionListener, RadioFragment.OnFragmentInteractionListener, Users.OnFragmentInteractionListener,
        UsuarioFragment.OnFragmentInteractionListener
{

    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private String nombre, mail;
    public int dispositivos_conectados = 0;
    TextView name, usrname, usrmail, dis_conec;
    ImageView usrimage;
    NavigationView navigationView;
    View hView;
    DrawerLayout drawer;
    FirebaseDatabase database;
    DatabaseReference mDB;
    List<Radios> radios;
    RecyclerView rv;
    Adaptador adaptador;
    TextView dis_num;

    public ProgressDialog Progressdialog;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Dialog dialog;
    myAsyncTask ast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buscarObjetos();
        //Progressdialog.show();

        //Firebase stuff
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDB = database.getReference().getRoot();
        ast = new myAsyncTask();

        if (mAuth.getCurrentUser() != null) {
            nombre = mAuth.getCurrentUser().getDisplayName();
            mail = mAuth.getCurrentUser().getEmail();
        }
        else {
            nombre = "";
            mail = "Someone@Example.com";
        }

//        rv.setLayoutManager(new LinearLayoutManager(this));
        radios = new ArrayList<>();
        adaptador = new Adaptador(radios, getApplicationContext(), getParent());
//        rv.setAdapter(adaptador);

        usrname.setText(nombre);
        usrmail.setText(mail);

        String token = preferences.getString("Token", "1");

        if (!token.equals("1")){
            addToken(token);
        }
        //usrimage.setImageURI(mAuth.getCurrentUser().getPhotoUrl());

        Picasso.with(getApplicationContext())
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(usrimage);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(MainActivity.this, LogIn.class));
                    finish();
                }
            }
        };

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);


        /*mDB.child(FirebaseReferences.Usuarios_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {
                    Usuarios user = snapshot.getValue(Usuarios.class);
                    String correoc = user.getEmail();
                    Boolean admn = user.getAdministrador();

                    if (mAuth.getCurrentUser().getEmail().equals(correoc) && !admn) {
                        //do some awesome stuff;
                        navigationView.getMenu().findItem(R.id.configuracion).setVisible(false);
                    }
                    else
                        navigationView.getMenu().findItem(R.id.configuracion).setVisible(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


    }

    private void addToken(final String token) {

        final String correo = mAuth.getCurrentUser().getEmail();


        mDB.child(FirebaseReferences.Usuarios_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {
                    Usuarios user = snapshot.getValue(Usuarios.class);
                    String correoc = user.getEmail();
                    String key = snapshot.getKey();



                    if (correo.equals(correoc)){

                        user.setToken(token);
                        Map<String, Object> postValues = user.toMap();
                        //mDB.child(FirebaseReferences.Usuarios_REFERENCE).child(key).child("token").setValue(token);
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/"+FirebaseReferences.Usuarios_REFERENCE+"/" + key, postValues);

                        mDB.updateChildren(childUpdates);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void buscarObjetos() {
        navigationView= (NavigationView) findViewById(R.id.nav_view);
        hView =  navigationView.getHeaderView(0);
        usrname = (TextView) hView.findViewById(R.id.usr_name);
        usrmail = (TextView) hView.findViewById(R.id.usr_mail);
        usrimage =(ImageView)hView.findViewById(R.id.usr_image);
        name = (TextView) findViewById(R.id.nombre);
        //dis_conec = (TextView) findViewById(R.id.disp_conectados);
        dis_num = (TextView) findViewById(R.id.disp_num);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Progressdialog = new ProgressDialog(this);

        //rv = (RecyclerView) findViewById(R.id.recyler);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("¿Estás seguro en salir de la app?");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                            Intent i = new Intent(getApplicationContext(), Whales.class);
//                            i.putExtra("rfc", RFC);
//                            startActivity(i);
                        }
                    });

            builder1.setNegativeButton(
                    "Si",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //dialog.cancel();
                            finish();
                        }
                    });

            android.support.v7.app.AlertDialog alert11 = builder1.create();
            alert11.show();

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
        if (dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment fragment = null;
        boolean FramentTransaction = true;

        if (FramentTransaction){
            fragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, fragment).commit();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        boolean fragmentTransaction = false;

        if (id == R.id.Radios) {
            // open  radios' activity
            fragment = new NanoStationFragment();
            fragmentTransaction = true;
        }else if (id == R.id.home){
            fragment = new HomeFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("rfc", rfc);
//            fragment.setArguments(bundle); use this to pass ip of radio the selected
            fragmentTransaction = true;
        }
        else if (id == R.id.logFile) {

        } else if (id == R.id.SpeedTest) {

        } else if (id == R.id.salir) {
            if (mAuth != null) {
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // ...
                            }
                        });
            }
            //editor.remove("administrador");
            //editor.apply();

        } else if (id == R.id.configuracion) {
            fragment = new Users();
            fragmentTransaction = true;
        }

        if (fragmentTransaction){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();
            item.setChecked(true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    private class myAsyncTask extends AsyncTask<Void, Void, Void>
    {
        String mensaje = "";
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getApplicationContext() , getResources().getString(R.string.Syncn), getResources().getString(R.string.wait), true);

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // protected SoapObject doInBackground(Integer... branchNumber){


            return null;
        }


        protected void onProgressUpdate(String values) {
            if (values != null) {
                // shows a toast for every value we get
                Toast.makeText(getApplicationContext(), values, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
