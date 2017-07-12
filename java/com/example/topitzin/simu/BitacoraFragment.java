package com.example.topitzin.simu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.topitzin.simu.objetos.Bitacora;
import com.example.topitzin.simu.objetos.BitacoraAdapter;
import com.example.topitzin.simu.objetos.FirebaseReferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BitacoraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class BitacoraFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    FirebaseDatabase database;
    DatabaseReference DBreference;
    BitacoraAdapter bitacoraAdapter;
    List<Bitacora> bitacoraList;
    RecyclerView recyclerView;
    EditText busqueda;

    public BitacoraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_bitacora, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerb);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        busqueda = (EditText) view.findViewById(R.id.search);

        bitacoraList = new ArrayList<>();
        bitacoraAdapter = new BitacoraAdapter(getActivity(), bitacoraList, getContext());
        // Inflate the layout for this fragment

        recyclerView.setAdapter(bitacoraAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        DBreference = database.getReference().getRoot();


        busqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.equals("")) {
                    bitacoraAdapter = new BitacoraAdapter(getActivity(), bitacoraList, getContext());
                    recyclerView.setAdapter(bitacoraAdapter);
                    bitacoraAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                charSequence = charSequence.toString().toLowerCase();

                if (!charSequence.equals("")) {

                    final List<Bitacora> listBitacora = new ArrayList<>();

                    for (int j = 0; j < bitacoraList.size(); j++) {

                        Bitacora bit = bitacoraList.get(j);
                        String evento = bit.getEvento().toLowerCase();

                        if (evento.contains(charSequence)) {
                            listBitacora.add(bit);
                        }
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    bitacoraAdapter = new BitacoraAdapter(getActivity(), listBitacora, getContext());
                    recyclerView.setAdapter(bitacoraAdapter);
                    bitacoraAdapter.notifyDataSetChanged();
                }
                else{
                    bitacoraAdapter = new BitacoraAdapter(getActivity(), bitacoraList, getContext());
                    recyclerView.setAdapter(bitacoraAdapter);
                    bitacoraAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        Query lastQuery = DBreference.child(FirebaseReferences.Bitacora_REFERENCE).orderByKey().limitToLast(15);
        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    bitacoraList.removeAll(bitacoraList);
                    for (DataSnapshot snapshot :
                            dataSnapshot.getChildren()) {
                        Bitacora bitacota = snapshot.getValue(Bitacora.class);
                        long mili = Long.parseLong(snapshot.getKey());
                        String miliseconds = snapshot.getKey();
                        Date date = new Date(mili);
                        String key = date.toString();
                        bitacota.setFecha(key);
                        bitacoraList.add(bitacota);
                    }
                    bitacoraAdapter.notifyDataSetChanged();
                }catch (Exception e){ Log.e("error",e.getMessage()); }
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
