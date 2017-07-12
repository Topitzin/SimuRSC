package com.example.topitzin.simu.objetos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.topitzin.simu.HomeFragment;
import com.example.topitzin.simu.NanoStationFragment;
import com.example.topitzin.simu.R;
import com.example.topitzin.simu.RadioFragment;
import com.example.topitzin.simu.radioIndividual;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.List;

/**
 * Created by topitzin on 17/03/2017.
 */
public class Adaptador extends RecyclerView.Adapter<Adaptador.RadiosViewHolder> {

    public Adaptador(List<Radios> radios, Context mcontext, Activity activity) {
        this.radios = radios;
        this.mCntx = mcontext;
        this.mActivity = activity;
    }

    Activity mActivity;
    List<Radios> radios;
    RecyclerView rv;
    Context mCntx;
    View v;
    private int lastPosition = -1;



    @Override
    public RadiosViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

         v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recycler, parent, false);

        rv = (RecyclerView) parent.findViewById(R.id.recycler);
        final Intent I = new Intent(mCntx, radioIndividual.class);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = rv.getChildLayoutPosition(v);
                Radios selectedRadio = radios.get(itemPosition);
                Boolean fragmentTransaction = true;

                String[] rM5 = new String[11];
                rM5[0] = selectedRadio.getIp();
                rM5[1] = selectedRadio.getNombre();
                rM5[2] = String.valueOf(selectedRadio.getRx());
                rM5[3] = String.valueOf(selectedRadio.getTx());
                rM5[4] = String.valueOf(selectedRadio.getSignal());
                rM5[5] = selectedRadio.getLlave();
                rM5[6] = String.valueOf(selectedRadio.getIntensidad());
                rM5[7] = String.valueOf(selectedRadio.getEstado());
                rM5[8] = String.valueOf(selectedRadio.getTiempo());
                rM5[9] = String.valueOf(selectedRadio.getIntensidad());
                rM5[10] = selectedRadio.getMac();

                String item = radios.get(itemPosition).getNombre();

                Fragment fragment = new RadioFragment();
//                Intent intent = new Intent(mCntx, Radio.class);
//                intent.putExtra("radioselected", rM5);
//                mCntx.startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putStringArray("radioselected", rM5);
                fragment.setArguments(bundle);
                I.putExtra("radioselected", rM5);


                FragmentManager fragmentManager = ((FragmentActivity) mCntx).getSupportFragmentManager();
                //fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

                //Toast.makeText(parent.getContext(), item, Toast.LENGTH_LONG).show();

                mCntx.startActivity(I);

            }
        });

        RadiosViewHolder holder = new RadiosViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RadiosViewHolder holder, int position) {
        Radios rad = radios.get(position);
        holder.txtvNombre.setText(rad.getNombre());
        holder.txtvIp.setText(rad.getIp());
        double señal = Math.abs(rad.getSignal());
        holder.bar.setMax(100);
        boolean conectado = rad.getEstado();

        Drawable bckgrndDr = new ColorDrawable(Color.BLUE);
        Drawable secProgressDr = new ColorDrawable(Color.GRAY);
        Drawable progressDr;
        LayerDrawable resultDr;

        if (señal < 50){
            progressDr = new ScaleDrawable(new ColorDrawable(Color.RED), Gravity.LEFT, 1, -1);
        }
        else if (señal < 90){
            progressDr = new ScaleDrawable(new ColorDrawable(Color.YELLOW), Gravity.LEFT, 1, -1);
        }
        else {
            progressDr = new ScaleDrawable(new ColorDrawable(Color.GREEN), Gravity.LEFT, 1, -1);
        }

        if (!conectado){
                holder.card.setBackground(mCntx.getResources().getDrawable(R.drawable.white_border_fail));
                holder.imgvRad.setImageDrawable(mCntx.getResources().getDrawable(R.drawable.image_failed));
        }
        else {
            holder.card.setBackground(mCntx.getResources().getDrawable(R.drawable.white_border_around));
            holder.imgvRad.setImageDrawable(mCntx.getResources().getDrawable(R.drawable.wifi_signal));
        }

        resultDr = new LayerDrawable(new Drawable[] { bckgrndDr, secProgressDr, progressDr });

        resultDr.setId(0, android.R.id.background);
        resultDr.setId(1, android.R.id.secondaryProgress);
        resultDr.setId(2, android.R.id.progress);

        holder.bar.setProgressDrawable(resultDr);
        holder.bar.setProgress((int)señal);

        View result = v;

        Animation animation = AnimationUtils.loadAnimation(mCntx,
                (position > lastPosition) ? R.anim.loadindown_anim: R.anim.loadinup_anim);

        result.startAnimation(animation);
        v.setTag(result);
        lastPosition = position;

    }


    @Override
    public int getItemCount() {
        return radios.size();
    }

    public static class RadiosViewHolder extends RecyclerView.ViewHolder{

        TextView txtvNombre, txtvIp;
        ProgressBar bar;
        ImageView imgvRad;
        RelativeLayout card;


        public RadiosViewHolder(View itemView) {
            super(itemView);
            txtvIp = (TextView) itemView.findViewById(R.id.radio_ip);
            txtvNombre= (TextView) itemView.findViewById(R.id.radio_name);
            imgvRad = (ImageView)itemView.findViewById(R.id.person_photo);
            card = (RelativeLayout) itemView.findViewById(R.id.relative);
            bar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }

    }

}
