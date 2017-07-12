package com.example.topitzin.simu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

public class NotificationLayout extends AppCompatActivity {

    TextView txt, txt2;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_layout);
        txt = (TextView) findViewById(R.id.txt);
        txt2 = (TextView) findViewById(R.id.titulo);
        bundle = getIntent().getExtras();

        String idOffer = "";

        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            idOffer = startingIntent.getStringExtra("x"); // Retrieve the id<
            txt.setText(idOffer);
        }

        if (bundle != null) {
            String mensaje = bundle.getString("msg", "");
            if (!mensaje.equals(""))
                txt.setText(mensaje);
        }

        txt2.setText(getResources().getString(R.string.disp_off));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            startActivity(new Intent(this, Main2Activity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
