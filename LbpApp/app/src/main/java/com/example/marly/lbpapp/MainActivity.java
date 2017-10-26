package com.example.marly.lbpapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.core.CvType.typeToString;
import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.opencv.imgcodecs.Imgcodecs.imread;

import com.example.marly.*;


public class MainActivity extends AppCompatActivity {

    private static final String TAG="Main";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView cadrephoto,photolue;
    public Mat imageOpencv = new Mat();
    Bitmap bitmapUser=null;
    final String MESSAGE = "type_image";
    String TYPE = null;


    //chargement de opencv

    static {
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "Opencv loaded");
        }
        else{
            Log.d(TAG, "Opencv not loaded");
        }

    }

    //on vérifie que l'application a le droit d'utiliser la caméra

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // vérification du droit d'écriture
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Quand on n'a pas la permission on avise l'utilisateur
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    //méthode qui vérifie que le téléphone dispose d'une caméra
    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    //méthode qui lance la caméra
    public void launchCamera(View view){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    //méthode qui capture une image, l'enregistre, calcule l'image LBP, crée son histogramme et la compare aux histogrammes de référence
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data"); //récupération de l'image capturée

            cadrephoto.setImageBitmap(photo);
            Bitmap image = null;
            String verdict = new String();


            LBP imLbp = new LBP();
            Histogramme hist = new Histogramme();

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            try {
               //enregistrement de l'image capturée
                String FILENAME = "image_file.jpeg";
                FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                fos.write(bytes.toByteArray());
                fos.close();


                //Log.d("SUCCESS", "enregistrement reussi");
                FileInputStream fileInputStream;

                //lecture de l'image
                fileInputStream = openFileInput(FILENAME);
                bitmapUser = BitmapFactory.decodeStream(fileInputStream);
                fileInputStream.close();

                Utils.bitmapToMat(bitmapUser,imageOpencv);
                Bitmap lbpBitmap = Bitmap.createBitmap(imageOpencv.width(),imageOpencv.height(), Bitmap.Config.ARGB_8888);
                Bitmap imgray = Bitmap.createBitmap(imageOpencv.width(),imageOpencv.height(), Bitmap.Config.ARGB_8888);
                Imgproc.cvtColor(imageOpencv, imageOpencv, Imgproc.COLOR_BGR2GRAY);
                Utils.matToBitmap(imageOpencv,imgray);
                //calcul de l'image LBP
                imLbp.setImageLbp(imageOpencv);
                Utils.matToBitmap(imLbp.getImageLbp(),lbpBitmap);
                //calcul  l'historamme et comparaison
                hist.setTab(imLbp.getImageLbp());
                photolue.setImageBitmap(lbpBitmap);
                TYPE=comparaison(histReference(),hist.getTab());

                //Log.d("SUCCESS", "Vous voyez les deux photos? COOL!");


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }






        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bouton = (Button)findViewById(R.id.capture);
        Button resultBtn = (Button)findViewById(R.id.resultButton);
        cadrephoto = (ImageView)findViewById(R.id.cadrephoto);
        photolue = (ImageView)findViewById(R.id.imageLue);
        if (!hasCamera())
            bouton.setEnabled(false);

        //lancement de la vue qui affiche le résultat
        resultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShowResultActivity.class);
                if(TYPE=="VEG"){
                    intent.putExtra(MESSAGE, "Vous venez de photographier une végétation!");
                    startActivity(intent);
                }
                else if (TYPE=="BAT"){
                    intent.putExtra(MESSAGE, "Vous venez de photographier un bâtiment!");
                    startActivity(intent);
                }
                else {
                    intent.putExtra(MESSAGE, "Désolée, nous ne pouvons identifier cette image.");
                    startActivity(intent);
                }

            }
        });

    }







    //méthode qui charge les histogrammes de la base de référence
    public int[][]histReference(){

        InputStream inputStream = getResources().openRawResource(R.raw.histogrames);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();
        String [] tab = new String[20];
        String[] words = new String[256];
        int [][]values = new int[20][256];

        try {

            int i=0;
            while (( line = buffreader.readLine()) != null) {


                tab[i]=line;
                i++;
            }


            buffreader.close();
            inputStream.close();



        } catch (IOException e) {

        }


        for (int i =0;i<20;i++){

            words = tab[i].split(",");


            for(int k=0; k<256; k++){
                values[i][k]= Integer.parseInt(words[k]);

            }

        }


        return values;


    }


    //méthode qui calcule la distance entre l'histogramme de l'image test et les histogrammes de référence
    public String comparaison(int [][]ref,int [] hisTest){

        int s=0, min=0, indic=0, type=0;
        int [][]tab = new int[20][2];
        String message = new String();

        for (int i=0;i<20;i++){
            for(int j=0;j<255;j++){
                s = s+ abs(ref[i][j]-hisTest[j]);
                indic=ref[i][255];
                tab[i][0]=s;
                tab[i][1]=indic;
            }
        }


        min = tab[0][0];
        for(int k=0;k<20;k++){

            if(tab[k][0]<min){
                min = tab[k][0];
                type = tab[k][1];
            }
        }

        if(type==0){
            message = "BAT";
        }
        else if (type==1){
            message= "VEG";
        }
        else
            message= "UNKNOWN";

        return message;

    }
}

