package com.example.marly.lbpapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Marly on 26/10/2017.
 * classe qui crée la vue qui affiche le résultat de la comparaison
 */

public class ShowResultActivity extends Activity {
    final String MESSAGE = "type_image";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showresult);

        Intent intent = getIntent();
        TextView afficher = (TextView)findViewById(R.id.resultmsg);


        if (intent !=null){
            String msg=intent.getStringExtra(MESSAGE);
            //Log.d("Donnée passée",msg);
            afficher.setText(intent.getStringExtra(MESSAGE));
        }
        else
            afficher.setText("intent null");
    }

    public void retour(View view){
        finish();
    }
}
