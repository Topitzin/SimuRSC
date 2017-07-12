package com.example.topitzin.simu.objetos;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.topitzin.simu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by topitzin on 27/04/2017.
 */

public class BitacoraAdapter extends RecyclerView.Adapter<BitacoraAdapter.BitacoraViewHolder>{

    Activity activity;
    List<Bitacora> bitacoraList;
    RecyclerView recyclerView;
    Context context;
    View view;

    public BitacoraAdapter(Activity activity, List<Bitacora> bitacoraList, Context context) {
        this.activity = activity;
        this.bitacoraList = bitacoraList;
        this.context = context;
    }

    @Override
    public BitacoraViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bitacora_row, parent, false);

        BitacoraViewHolder holder = new BitacoraViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BitacoraViewHolder holder, int position) {

        Bitacora log = bitacoraList.get(position);

        holder.evento.setText(log.getEvento());
        holder.fecha.setText(log.getFecha());
        holder.desc.setText(log.getDescripcion());

    }

    @Override
    public int getItemCount() {
        return bitacoraList.size();
    }

    public static class BitacoraViewHolder extends RecyclerView.ViewHolder{

        TextView fecha, evento, desc;

        public BitacoraViewHolder(View itemView) {
            super(itemView);
            fecha = (TextView) itemView.findViewById(R.id.fecha);
            desc = (TextView) itemView.findViewById(R.id.desc);
            evento = (TextView) itemView.findViewById(R.id.evento);
        }
    }

}
