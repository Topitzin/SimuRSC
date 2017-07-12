package com.example.topitzin.simu;

import android.app.Application;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.topitzin.simu.objetos.FirebaseReferences;
import com.example.topitzin.simu.objetos.Radios;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 */
public class HomeFragment extends Fragment {

    private String nombre, mail = "";
    private OnFragmentInteractionListener mListener;
    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    FirebaseDatabase database;
    DatabaseReference mDB;
    public int dispositivos_conectados = 0;
    TextView dis_num, name;
    public ProgressDialog Progressdialog;
    CardView card;
    private CardView card2;
    DatabaseReference sistemaref;
    DatabaseReference activo, alarmas;
    private TextView dis_num3, sistema, notifi;
    SwitchCompat sw1, sw2;
    SharedPreferences preferences;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        mDB = database.getReference().getRoot();

        if (mAuth.getCurrentUser() != null) {
            nombre = mAuth.getCurrentUser().getDisplayName();
        }

        View view = buscarObjetos(inflater, container);
        View view1 = view.findViewById(R.id.content_main);


        name.setText(nombre);


        return view;
    }

    private View buscarObjetos(LayoutInflater inflater, final ViewGroup container) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        dis_num = (TextView) view.findViewById(R.id.disp_num);
        name = (TextView) view.findViewById(R.id.nombre);
        Progressdialog = new ProgressDialog(getContext());
        dis_num3 = (TextView)view.findViewById(R.id.disp_num3);
        card = (CardView) view.findViewById(R.id.card1);
        card2 = (CardView) view.findViewById(R.id.card2);
        //Progressdialog.setMessage(getContext().getResources().getString(R.string.sincronizando));
        Progressdialog.setIndeterminate(true);
        Progressdialog.setCancelable(false);
        final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.container);
        sw1 = (SwitchCompat) view.findViewById(R.id.sw_sistema);
        sw2 = (SwitchCompat) view.findViewById(R.id.sw_notify);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        sistemaref = mDB.child(FirebaseReferences.Sistema_REFERENCE);
        activo = sistemaref.child("activado");
        alarmas = sistemaref.child("alarmas");

        sistema = (TextView) view.findViewById(R.id.txt_sistema);
        notifi = (TextView) view.findViewById(R.id.txt_nitify);



        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1, true);
            }
        });


        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(3, true);
            }
        });

        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activo.setValue(b);
                alarmas.setValue(b);
            }
        });

        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (sw1.isChecked()){
                    alarmas.setValue(b);
                }
                else
                    sw2.setChecked(false);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(getActivity(), LogIn.class));
                }
            }
        };

        boolean admin = preferences.getBoolean("administrador", false);

        if (!admin){
            sw1.setVisibility(view.INVISIBLE);
            sw2.setVisibility(view.INVISIBLE);
            sistema.setVisibility(view.INVISIBLE);
            notifi.setVisibility(view.INVISIBLE);
        }
        else {
            sw1.setVisibility(view.VISIBLE);
            sw2.setVisibility(view.VISIBLE);
            sistema.setVisibility(view.VISIBLE);
            notifi.setVisibility(view.VISIBLE);
        }

        mDB.child(FirebaseReferences.Radios_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //radios.removeAll(radios)
                dispositivos_conectados = 0;

                try {
                    for (DataSnapshot DS :
                            dataSnapshot.getChildren()) {
                        Radios radio = DS.getValue(Radios.class);
                        //radio.setIp(DS.getKey());
                        //radios.add(radio);
                        if (radio.getEstado())
                            dispositivos_conectados++;
                    }

                    //adaptador.notifyDataSetChanged();

                    dis_num.setText("\n" + String.valueOf(dispositivos_conectados));
                }
                catch (Exception e){ }

                if (Progressdialog.isShowing())
                    Progressdialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query lastQuery = mDB.child(FirebaseReferences.Bitacora_REFERENCE).orderByKey().limitToLast(1);
        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String error = ""; //dataSnapshot.getChildren().iterator().toString();

                try {
                    String tipoError = dataSnapshot.getChildren().iterator().next().getValue().toString();
                    tipoError = tipoError.replace("{", "");
                    tipoError = tipoError.replace("}", "");

                    String[] descripcionTipo = tipoError.split("=");

                    dis_num3.setText(descripcionTipo[1] + " " + descripcionTipo[2]);
                }
                catch (Exception e){ }

                if (Progressdialog.isShowing())
                    Progressdialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        activo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean sistema = dataSnapshot.getValue(Boolean.class);

                sw1.setChecked(sistema);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        alarmas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean sistema = dataSnapshot.getValue(Boolean.class);

                sw2.setChecked(sistema);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
