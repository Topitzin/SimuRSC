package com.example.topitzin.simu;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.topitzin.simu.objetos.FirebaseReferences;
import com.example.topitzin.simu.objetos.Radios;
import com.example.topitzin.simu.objetos.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RadioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RadioFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    String[] radio;
    TextView nombre, ip, rx, tx, señal;
    AlertDialog.Builder alert;
    AlertDialog.Builder alertE;
    String key;
    View alertLayout;
    Bundle bundle;
    FloatingActionButton atras, modificar, eliminar;
    public FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDB;
    Radios estacion;
    private TextView estado;
    private TextView tiempo;

    public RadioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_radio, container, false);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        mDB = database.getReference().getRoot();

        bundle = this.getArguments();
        buscarObjetos(view);

        if (bundle != null) {
            radio = bundle.getStringArray("radioselected");
            llenarespacios(radio);
        }


        eventos();
        crearAlerta(inflater, container, view);
        crearElimnar(view);



        return view;
    }

    private void crearElimnar(View view) {
        alertE = new AlertDialog.Builder(view.getContext());
        alertE.setTitle(getResources().getString(R.string.confirm_eliminar));
        alertE.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do something awesome!
                mDB.child(FirebaseReferences.Radios_REFERENCE).child(key).removeValue();
                atras.callOnClick();
            }
        });

        alertE.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
    }

    private void eventos() {
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new NanoStationFragment();
                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
            }
        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertLayout.getParent() != null){
                    ((ViewGroup)alertLayout.getParent()).removeView(alertLayout);
                }
                alert.show();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertLayout.getParent() != null){
                    ((ViewGroup)alertLayout.getParent()).removeView(alertLayout);
                }
                alertE.show();
            }
        });
    }

    private void crearAlerta(LayoutInflater inflater, ViewGroup container, View view) {
        alertLayout = inflater.inflate(R.layout.ip_layout,null);
        alert = new AlertDialog.Builder(view.getContext());
        final EditText ed_ip = (EditText) alertLayout.findViewById(R.id.et_username);
        if (radio != null){
            ed_ip.setText(""+radio[0]);
            alert.setTitle(radio[1] + " / " + key);
        }



        alert.setView(alertLayout);
        alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newip = ed_ip.getText().toString();
                // Do something awesome!
                //mDB.child(FirebaseReferences.Radios_REFERENCE).child(key).child("ip").se
                if (!newip.equals("")) {
                    estacion = new Radios(radio[1], newip, Integer.parseInt(radio[2]), Integer.parseInt(radio[3]), Double.parseDouble(radio[4]), true, Integer.parseInt(radio[6]), "",0);


                    Map<String, Object> postValues = estacion.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/"+FirebaseReferences.Radios_REFERENCE+"/" + key, postValues);

                    mDB.updateChildren(childUpdates);
                    atras.callOnClick();
                }

            }
        });

        alert.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
    }

    private void buscarObjetos(View view) {
        nombre = (TextView) view.findViewById(R.id.nombre);
        ip = (TextView) view.findViewById(R.id.ip);
        rx = (TextView) view.findViewById(R.id.Rx);
        tx = (TextView) view.findViewById(R.id.Tx);
        señal = (TextView) view.findViewById(R.id.señal);
        estado = (TextView) view.findViewById(R.id.estado);
        tiempo = (TextView) view.findViewById(R.id.tiempo);


        if (mAuth.getCurrentUser() != null)
            //buscar corro de adm en la base de datos y/o guardarlo en preferencias
            mDB.child(FirebaseReferences.Usuarios_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot :
                            dataSnapshot.getChildren()) {
                        Usuarios user = snapshot.getValue(Usuarios.class);
                        String correoc = user.getEmail();
                        Boolean admn = user.getAdministrador();

                        if (mAuth.getCurrentUser().getEmail().equals(correoc) && !admn) {
                            modificar.hide();
                            eliminar.hide();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }


    private void llenarespacios(String[] radio) {
        nombre.setText(radio[1]);
        ip.setText("Ip: "+radio[0]);
        rx.setText("Tasa Rx: "+radio[2]);
        tx.setText("Tasa Tx: "+radio[3]);
        señal.setText("Señal: "+radio[4]);
        key = radio[5];
        if (radio[7].equals("true"))
            estado.setText("Estado: Conectado");
        else
            estado.setText("Estado: Desconectado");

        tiempo.setText(calculartiempo(radio[8]));
    }

    private String calculartiempo(String s) {
        int time = Integer.parseInt(s);

        int horas = ((time/1000)/60)/60;
        int min = (horas * 60) - ((time/1000)/60);
        min = Math.abs(min);

        return "Horas: " + String.valueOf(horas) + ", Minutos: " + String.valueOf(min) + " conectado";
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
