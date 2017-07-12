package com.example.topitzin.simu.objetos;

import android.app.Activity;
import android.graphics.Color;
import android.widget.Toast;

import com.example.topitzin.simu.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Gerardo on 20/04/2017.
 */

public class Graph {

    GraphView graph;
    DataPoint[] data;
    DataPoint[] data1;
    Activity a;

    List<SpeedTest> s;

    public Graph(Activity activity, DataPoint[] d, List<SpeedTest> sp){
        a = activity;
        data = d;
        s = sp;
        graph = (GraphView) activity.findViewById(R.id.graph);
        initGraph(graph);
    }

    public Graph(Activity activity, DataPoint[]d, DataPoint[] d1, List<SpeedTest> sp){
        a = activity;
        data = d;
        data1 = d1;
        s = sp;
        graph = (GraphView) activity.findViewById(R.id.graph);
        initGraph(graph);
    }

    public void initGraph(GraphView graph) {

        // generate Dates

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(data);

        series.setDrawBackground(true);
        //series.setDrawDataPoints(true);
        //series.setDataPointsRadius(5);
        series.setColor(Color.BLUE);
        graph.removeAllSeries();
        graph.addSeries(series);


        if (data1 != null){
            LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(data1);

            series1.setDrawBackground(true);
            series1.setColor(Color.DKGRAY);
            graph.addSeries(series1);

            series1.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {

                    String fl  = s.get( (int) dataPoint.getX() ).getFecha();

                    long mili = Long.parseLong(fl);
                    Date date = new Date(mili);
                    String key = date.toString();


                    Toast.makeText(a.getApplicationContext(),
                            key +"\nBajada: " + dataPoint.getY() + " "
                            , Toast.LENGTH_SHORT).show();
                }
            });

        }
        // set date label formatter
        //graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graph.getContext()));
        //graph.getGridLabelRenderer().setNumHorizontalLabels(3);


        // set manual x bounds to have nice steps
        graph.getViewport().setMaxX( data[data.length -1].getX());
        graph.getViewport().setMinX( data[data.length - (data.length/2)].getX());
        //graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not nessecary
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Subida y Bajada");

        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setGridColor(Color.WHITE);
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);

        //graph.getGridLabelRenderer().setHumanRounding(false);

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {

                String fl  = s.get( (int) dataPoint.getX() ).getFecha();

                long mili = Long.parseLong(fl);
                Date date = new Date(mili);
                String key = date.toString();


                Toast.makeText(a.getApplicationContext(),
                       key +"\nSubida: " + dataPoint.getY() + " "
                        , Toast.LENGTH_SHORT).show();
            }
        });





        //graph.getLegendRenderer().setVisible(true);
        //graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

    }


}
