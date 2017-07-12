package com.example.topitzin.simu;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsuarioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class UsuarioFragment extends Fragment {

    private OnFragmentInteractionListener mListener;




    //////////////////////////////////////////////////////////UNUSED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    String[] usuario;
    AlertDialog.Builder alert;
    String key;
    View alertLayout;
    Bundle bundle;
    FloatingActionButton atras, modificar, eliminar;
    public FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDB;
    TextView correo, nombre;
    SwitchCompat push, email, admin;
    Boolean pushValue, emailValue, adminValue;

    public UsuarioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        mDB = database.getReference().getRoot();

        bundle = this.getArguments();
        buscarObjetos(view);

        if (bundle != null) {
            usuario = bundle.getStringArray("userSelected");
            llenarespacios(usuario);
        }


        eventos(view);

        return view;
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

    private void crearAlerta(View view, String titulo, final int id) {

        alert = new AlertDialog.Builder(view.getContext());
        alert.setTitle(titulo);
        alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do something awesome!
                if (id == 2) {
                    try { mDB.child(FirebaseReferences.Usuarios_REFERENCE).child(key).removeValue(); }
                    catch (Exception e) { }

                    atras.callOnClick();
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
                    user.setEnableEmail(emailValue);
                    user.setEnablePush(pushValue);

                    try {
                        Map<String, Object> postValues = user.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/" + FirebaseReferences.Usuarios_REFERENCE + "/" + key, postValues);

                        mDB.updateChildren(childUpdates);
                    } catch (Exception e){ }
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

    private void eventos(final View view) {
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Users();
                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
            }
        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearAlerta(view, getResources().getString(R.string.modificar_usr), 1);
                alert.show();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearAlerta(view, getResources().getString(R.string.eliminar_usr), 2);
                alert.show();
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

    private void buscarObjetos(View view) {


        nombre = (TextView) view.findViewById(R.id.nombre);
        correo = (TextView) view.findViewById(R.id.correo);
        push = (SwitchCompat) view.findViewById(R.id.push);
        email = (SwitchCompat) view.findViewById(R.id.email);
        admin = (SwitchCompat) view.findViewById(R.id.admin);



        if (mAuth.getCurrentUser() != null)
            //buscar corro de adm en la base de datos y/o guardarlo en preferencias
            mDB.child(FirebaseReferences.Usuarios_REFERENCE).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
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
                    }catch (Exception e){ }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

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
