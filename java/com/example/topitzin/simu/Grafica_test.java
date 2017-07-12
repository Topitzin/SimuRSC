package com.example.topitzin.simu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.topitzin.simu.objetos.FirebaseReferences;
import com.example.topitzin.simu.objetos.Graph;
import com.example.topitzin.simu.objetos.SpeedTest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.series.DataPoint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Grafica_test extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference DBreference;
    List<SpeedTest> speedtest;
    Graph g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_test);

        database = FirebaseDatabase.getInstance();
        DBreference = database.getReference().getRoot();
        speedtest = new ArrayList<>();

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

                DataPoint[] d = new DataPoint[speedtest.size()];
                for (int i = 0; i < speedtest.size(); i++) {
                    SpeedTest as = speedtest.get(i);
                    d[i] = new DataPoint(i, as.getPing());


                }
                g = new Graph(Grafica_test.this, d, speedtest);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
