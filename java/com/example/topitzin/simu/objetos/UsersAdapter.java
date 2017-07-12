package com.example.topitzin.simu.objetos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.topitzin.simu.MainActivity;
import com.example.topitzin.simu.R;
import com.example.topitzin.simu.Users;
import com.example.topitzin.simu.UsuarioFragment;
import com.example.topitzin.simu.UsuarioIndividual;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by topitzin on 06/04/2017.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    public UsersAdapter(Activity mActivity, List<Usuarios> users, Context mCntx, FirebaseAuth myAuth) {
        this.mActivity = mActivity;
        this.users = users;
        this.mCntx = mCntx;
        this.mauth = myAuth;
    }

    Activity mActivity;
    List<Usuarios> users;
    RecyclerView rv;
    Context mCntx;
    boolean permission = true;
    FirebaseAuth mauth;
    FirebaseDatabase database;
    DatabaseReference reference;
    SharedPreferences preferences;
    View v;
    private int lastPosition = -1;

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row_layout, parent, false);

        rv = (RecyclerView) parent.findViewById(R.id.recyclerU);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().getRoot();
        preferences = PreferenceManager.getDefaultSharedPreferences(mCntx);
        final boolean admin = preferences.getBoolean("administrador", false);

        final Intent I = new Intent(mCntx, UsuarioIndividual.class);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = rv.getChildLayoutPosition(v);
                Usuarios userSelected = users.get(itemPosition);
                Boolean fragmentTransaction = true;

                String[] rM5 = new String[7];
                rM5[0] = userSelected.getName();
                rM5[1] = userSelected.getEmail();
                rM5[2] = String.valueOf(userSelected.getAdministrador());
                rM5[3] = String.valueOf(userSelected.getEnableEmail());
                rM5[4] = String.valueOf(userSelected.getEnablePush());
                rM5[5] = userSelected.getLlave();
                rM5[6] = userSelected.getToken();

                String item = users.get(itemPosition).getNombre();


                Fragment fragment = new UsuarioFragment();//otro fragment
//                Intent intent = new Intent(mCntx, Radio.class);
//                intent.putExtra("radioselected", rM5);
//                mCntx.startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putStringArray("userSelected", rM5);
                fragment.setArguments(bundle);
                I.putExtra("userSelected", rM5);


                if (admin) {
                    FragmentManager fragmentManager = ((FragmentActivity) mCntx).getSupportFragmentManager();
                    //fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
                    mCntx.startActivity(I);

                }else {
                    Snackbar.make(v,"No tienes permisos de acceso", 1300).show();
                }

                //Toast.makeText(parent.getContext(), item, Toast.LENGTH_LONG).show();

            }
        });
        UsersViewHolder holder = new UsersViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(UsersViewHolder holder, int position) {
        Usuarios user = users.get(position);
        holder.txtEmail.setText(user.getEmail());
        holder.txtvNombre.setText(user.getName());
        if (user.getAdministrador()){
            holder.txtAdm.setText("Administrador");
        }
        else
            holder.txtAdm.setText("");
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        TextView txtvNombre, txtEmail, txtAdm;
        ImageView imgvRad;

        public UsersViewHolder(View itemView) {
            super(itemView);
            txtEmail = (TextView) itemView.findViewById(R.id.user_email);
            txtvNombre= (TextView) itemView.findViewById(R.id.user_name);
            imgvRad = (ImageView)itemView.findViewById(R.id.user_icon);
            txtAdm = (TextView) itemView.findViewById(R.id.user_adm);

        }

    }
}
