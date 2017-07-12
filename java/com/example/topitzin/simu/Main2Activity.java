package com.example.topitzin.simu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import com.example.topitzin.simu.objetos.FirebaseReferences;
import com.example.topitzin.simu.objetos.Radios;
import com.example.topitzin.simu.objetos.SecctionPagerAdapter;
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

import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class Main2Activity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
        NanoStationFragment.OnFragmentInteractionListener, RadioFragment.OnFragmentInteractionListener ,
        Users.OnFragmentInteractionListener, UsuarioFragment.OnFragmentInteractionListener,
        SpeedTestFragment.OnFragmentInteractionListener, BitacoraFragment.OnFragmentInteractionListener {


    SecctionPagerAdapter mPageAdapter;
    private ViewPager mViewPager;
    private SharedPreferences preferences;

    private String mail;
    FirebaseDatabase database;
    DatabaseReference mDB;
    DatabaseReference adminis;
    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog Progressdialog;
    MenuItem mMenu;
    private SwitchCompat sw, sw1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        Progressdialog = new ProgressDialog(this);
        Progressdialog.setIndeterminate(true);
        Progressdialog.setCancelable(false);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(false); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)


        mPageAdapter = new SecctionPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setUpViewPager(mViewPager);

        TabLayout tabLayout  = (TabLayout)findViewById(R.id.toolbarup);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setIcon(R.drawable.wifi_signal4);
        tabLayout.getTabAt(4).setIcon(R.drawable.settings6);
        tabLayout.getTabAt(3).setIcon(R.drawable.calendar1);
        tabLayout.getTabAt(2).setIcon(R.drawable.rss);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDB = database.getReference().getRoot();
        DatabaseReference sistema = mDB.child(FirebaseReferences.Sistema_REFERENCE);
        DatabaseReference activo = sistema.child("activado");

        sw = (SwitchCompat) findViewById(R.id.sw_sistema);
        sw1 = (SwitchCompat) findViewById(R.id.sw_notify);

//
//        if(esTablet(getApplicationContext())){ //tablet
//            for (int i = 0; i < tabLayout.getTabCount(); i++)
//            {
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(60, 60); //set new width & Height
//                params.gravity = Gravity.CENTER; //set gravity back to center
//                tabLayout.getChildAt(i).setLayoutParams(params);//set ur new params
//
//            }
//        }else{ // telefono
//            for (int i = 0; i < tabLayout.getTabCount(); i++)
//            {
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(30, 30); //set new width & Height
//                params.gravity = Gravity.CENTER; //set gravity back to center
//                tabLayout.getChildAt(i).setLayoutParams(params);//set ur new params
//
//            }
//        }

        String token = preferences.getString("Token", "1");

        addToken(token);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(Main2Activity.this, LogIn.class));
                    finish();
                }
            }
        };



    }

    public static boolean esTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void addToken(final String token) {
        final String correo = mAuth.getCurrentUser().getEmail();


        mDB.child(FirebaseReferences.Usuarios_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    for (DataSnapshot snapshot :
                            dataSnapshot.getChildren()) {
                        Usuarios user = snapshot.getValue(Usuarios.class);
                        String correoc = user.getEmail();
                        String key = snapshot.getKey();


                        if (correo.equals(correoc)) {

                            mDB.child(FirebaseReferences.Usuarios_REFERENCE).child(key).child("administrador").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean adm = dataSnapshot.getValue(Boolean.class);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("administrador", adm);
                                    editor.apply();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            break;
                        }
                    }
                }
                catch (Exception e) { }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpViewPager(ViewPager mViewPager) {
        SecctionPagerAdapter adapter = new SecctionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new NanoStationFragment());
        adapter.addFragment(new SpeedTestFragment());
        adapter.addFragment(new BitacoraFragment());
        adapter.addFragment(new Users());

        mViewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        mMenu = menu.findItem(R.id.wifiloss);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    if (Progressdialog.isShowing()){
                        Progressdialog.dismiss();
                    }
                    toolbar.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.head, null));
                    Progressdialog.setMessage(getResources().getText(R.string.sincronizando));
                    Progressdialog.show();
                    mMenu.setVisible(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Progressdialog.dismiss();
                        }
                    }, 3000);

                } else {
                    Progressdialog.setMessage("Buscando conexión a internet");
                    toolbar.setBackgroundColor(Color.RED);
                    Progressdialog.show();
                    mMenu.setVisible(true);
                    mMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Progressdialog.dismiss();
                        }
                    }, 5000);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_main_setting:
                cerrar_sesion();
                break;
            case R.id.wifiloss:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
        }
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void cerrar_sesion(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(getResources().getString(R.string.ask_cerrar_sesion));
        alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
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
            }
        });

        alert.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }
}
