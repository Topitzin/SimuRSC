package com.example.topitzin.simu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.icu.lang.UScript;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.example.topitzin.simu.objetos.UsersAdapter;
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
 * {@link Users.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Users extends Fragment {

    private Users.OnFragmentInteractionListener mListener;
    List<Usuarios> users;
    RecyclerView rv;
    FirebaseDatabase database;
    DatabaseReference DBreference;
    AlertDialog.Builder alert;
    FloatingActionButton fab;
    SharedPreferences preferences;
    UsersAdapter adaptador;
    View alertLayout;
    private FirebaseAuth mAuth;

    public Users() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        mAuth = FirebaseAuth.getInstance();
        rv= (RecyclerView) view.findViewById(R.id.recyclerU);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        alertLayout = inflater.inflate(R.layout.correo_layout,null);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        fab = (FloatingActionButton) view.findViewById(R.id.fabadd);

        if (!preferences.getBoolean("administrador",false)){
            fab.hide();
        }
        users = new ArrayList<>();

        adaptador = new UsersAdapter(getActivity(), users, getContext(), mAuth);
        rv.setAdapter(adaptador);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        DBreference = database.getReference().getRoot();
        alert = new AlertDialog.Builder(view.getContext());


        DBreference.child(FirebaseReferences.Usuarios_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    users.removeAll(users);
                    for (DataSnapshot snapshot :
                            dataSnapshot.getChildren()) {
                        Usuarios user = snapshot.getValue(Usuarios.class);
                        user.setLlave(snapshot.getKey());
                        users.add(user);
                    }
                    adaptador.notifyDataSetChanged();
                }catch (Exception e){ }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertLayout.getParent() != null){
                    ((ViewGroup)alertLayout.getParent()).removeView(alertLayout);
                }
                mostrarAlerta(alert);
            }
        });
    }

    private void mostrarAlerta(AlertDialog.Builder alert) {
        alert.setTitle(getResources().getString(R.string.email));
        final EditText ed_ip = (EditText) alertLayout.findViewById(R.id.et_username);
        alert.setView(alertLayout);
        alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do something awesome
                final String correo = ed_ip.getText().toString();
                if (!correo.equals("")){
                    final Usuarios usuario = new Usuarios(correo,"","0",true, true, false);

                    DBreference.child(FirebaseReferences.Usuarios_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean existe = false;
                            for (DataSnapshot snapshot :
                                    dataSnapshot.getChildren()) {
                                Usuarios user = snapshot.getValue(Usuarios.class);
                                if (user.getEmail().equals(correo)){
                                    existe = true;
                                    break;
                                }
                            }
                            if (!existe){
                                DBreference.child(FirebaseReferences.Usuarios_REFERENCE).push().setValue(usuario);
                            }
                            else
                                Toast.makeText(getContext(), getResources().getString(R.string.CorreoRepetido), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }
        });

        alert.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
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
