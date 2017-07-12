package com.example.topitzin.simu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.topitzin.simu.objetos.Adaptador;
import com.example.topitzin.simu.objetos.FirebaseReferences;
import com.example.topitzin.simu.objetos.Radios;
import com.example.topitzin.simu.objetos.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NanoStationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NanoStationFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    List<Radios> radios;
    RecyclerView rv;
    FirebaseDatabase database;
    DatabaseReference DBreference;
    DatabaseReference radios_reference;
    Adaptador adaptador;
    FloatingActionButton fab;
    AlertDialog.Builder alert;
    SharedPreferences preferences;
    View alertLayout;
    private FirebaseAuth mAuth;
    // Create TextView
    //TextView input;

    public NanoStationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nano_station, container, false);

        alertLayout = inflater.inflate(R.layout.ip_layout,null);
        alert = new AlertDialog.Builder(view.getContext());
        final EditText ed_ip = (EditText) alertLayout.findViewById(R.id.et_username);
        mAuth = FirebaseAuth.getInstance();

        rv= (RecyclerView) view.findViewById(R.id.recycler);
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        fab = (FloatingActionButton) view.findViewById(R.id.add);
        radios = new ArrayList<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        adaptador = new Adaptador(radios, getContext(), getActivity());
        rv.setAdapter(adaptador);

        alert.setTitle(getResources().getString(R.string.nuevaip));

        alert.setView(alertLayout);
        alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do something awesome!
                try {
                    final String ipAddress = ed_ip.getText().toString();
                    final Radios estacion = new Radios("", "", 0, 0, 0, false, 0, "", 0);
                    if (!ipAddress.equals("")) {
                        if (ip_valida(ipAddress)) {
                            Toast.makeText(getContext(), getResources().getString(R.string.Ipnovalida), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            DBreference.child(FirebaseReferences.Radios_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean existe = false;
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        Radios r1 = data.getValue(Radios.class);
                                        String ip = r1.getIp();
                                        if (ip.equals(ipAddress)) {
                                            existe = true;
                                        }
                                    }
                                    if (!existe) {
                                        estacion.setIp(ipAddress);
                                        DBreference.child(FirebaseReferences.Radios_REFERENCE).push().setValue(estacion);
                                    }
                                    else {
                                        Toast.makeText(getContext(), getResources().getString(R.string.IpRepetida), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
                catch (Exception e){ }
            }
        });

        alert.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });


        return view;
    }

    public boolean ip_valida(String ipAddress){

        if ((ipAddress.contains("N") || ipAddress.contains("+") || ipAddress.contains("-") || ipAddress.contains("#")
                || ipAddress.contains("(") || ipAddress.contains(")") || ipAddress.contains("/") || ipAddress.contains(",")
                || ipAddress.contains(";") || ipAddress.contains(" ")) || ipAddress.endsWith(".")) {
            return true;
        }
        return false;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        DBreference = database.getReference().getRoot();
        radios_reference = DBreference.child(FirebaseReferences.Radios_REFERENCE);


        radios_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    radios.removeAll(radios);
                    for (DataSnapshot snapshot :
                            dataSnapshot.getChildren()) {
                        Radios radio = snapshot.getValue(Radios.class);
                        radio.setLlave(snapshot.getKey());
                        radios.add(radio);
                    }
                    adaptador.notifyDataSetChanged();
                }
                catch (Exception e){ }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        radios_reference.keepSynced(true);

        boolean admin = preferences.getBoolean("administrador", false);

        if (!admin){
            fab.hide();
        }
//        if (mAuth.getCurrentUser() != null){
//            //buscar correo de adm en la base de datos y/o guardarlo en preferencias
//            DBreference.child(FirebaseReferences.Usuarios_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    try {
//                        for (DataSnapshot snapshot :
//                                dataSnapshot.getChildren()) {
//                            Usuarios user = snapshot.getValue(Usuarios.class);
//                            String correoc = user.getEmail();
//                            Boolean admn = user.getAdministrador();
//
//                            if (mAuth.getCurrentUser().getEmail().equals(correoc) && !admn) {
//                                fab.hide();
//                            }
//                        }
//                    }
//                    catch (Exception e){ }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//    }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertLayout.getParent() != null){
                    ((ViewGroup)alertLayout.getParent()).removeView(alertLayout);
                }
                alert.show();

            }
        });
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
