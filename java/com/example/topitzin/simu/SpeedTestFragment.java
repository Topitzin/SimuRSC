package com.example.topitzin.simu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.topitzin.simu.objetos.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.series.DataPoint;

import java.io.BufferedReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpeedTestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpeedTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeedTestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters



    private OnFragmentInteractionListener mListener;
    List<SpeedTest> speedtest;
    RecyclerView rv;
    FirebaseDatabase database;
    DatabaseReference DBreference;
    FloatingActionButton fab;
    AlertDialog.Builder alert;
    View alertLayout;
    private Button ver_grafica,  ver_graficab;
    Graph g;


    TextView p, s, b;

    private FirebaseAuth mAuth;

    public SpeedTestFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpeedTestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpeedTestFragment newInstance(String param1, String param2) {
        SpeedTestFragment fragment = new SpeedTestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_speed_test, container, false);
        speedtest = new ArrayList<>();
        return  view;

    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        DBreference = database.getReference().getRoot();


        ver_grafica = (Button) getActivity().findViewById(R.id.btn_grafica);
        ver_graficab = (Button) getActivity().findViewById(R.id.btn_graficaBjada);
        p = (TextView) getActivity().findViewById(R.id.txtping);
        b = (TextView) getActivity().findViewById(R.id.txtbajada);
        s = (TextView) getActivity().findViewById(R.id.txtsubida);


        ver_grafica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Grafica_test.class));
            }
        });

        ver_graficab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), grafica_bajada.class));
            }
        });


        DBreference.child(FirebaseReferences.SpeedTest_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                speedtest.removeAll(speedtest);
                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {
                    SpeedTest test = snapshot.getValue(SpeedTest.class);
                    test.setFecha(snapshot.getKey());

                    speedtest.add(test);
                }

                try {
                    SpeedTest a = speedtest.get(speedtest.size() - 1);

                    p.setText(String.valueOf(a.getPing()) + " ms");
                    b.setText(String.valueOf(a.getBajada()) + " Mbps");
                    s.setText(String.valueOf(a.getSubida()) + " Mbps");
                }catch (Exception e){}


//                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//                DataPoint[] d = new DataPoint[speedtest.size()];
//                for (int i = 0; i < speedtest.size(); i++) {
//                    SpeedTest as = speedtest.get(i);
//                    String f = as.getFecha().substring(0, 8);
//                    try {
//                        Date dat = (Date) formatter.parse(f);
//                        d[i] = new DataPoint(i, as.getPing());
//                        //
//                    }
//                    catch (Exception e){
//
//                    }
//
//                }
//                g = new Graph(getActivity(), d, speedtest);


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
