package com.example.marly.lbpapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    static {
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "Opencv loaded");
        }
        else{
            Log.d(TAG, "Opencv not loaded");
        }

    }

    public Mat imageOpencv = new Mat();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap image = null;
        String verdict= new String();


        LBP imLbp = new LBP();
        Histogramme hist = new Histogramme();
        try {
            image = BitmapFactory.decodeResource(getResources(), R.drawable.imagetest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.bitmapToMat(image,imageOpencv);
        Bitmap lbpBitmap = Bitmap.createBitmap(imageOpencv.width(),imageOpencv.height(), Bitmap.Config.ARGB_8888);
        Bitmap imgray = Bitmap.createBitmap(imageOpencv.width(),imageOpencv.height(), Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor(imageOpencv, imageOpencv, Imgproc.COLOR_BGR2GRAY);
        Utils.matToBitmap(imageOpencv,imgray);
        imLbp.setImageLbp(imageOpencv);
        Utils.matToBitmap(imLbp.getImageLbp(),lbpBitmap);
        hist.setTab(imLbp.getImageLbp());

       // ImageView vue = (ImageView)findViewById(R.id.vue);
        //test affichage image lbp
        //vue.setImageBitmap(lbpBitmap);

        //chargement des histogrammes des images de la base d'apprentissage
        verdict=comparaison(histReference(),hist.getTab());

        //test affichage histogramme
        /*for(int i= 0; i<255;i++){

            System.out.println("nb pixels"+i+" : "+hist.getTab()[i]);

        }*/






        TextView text = (TextView) findViewById(R.id.sample_text);
        text.setText(verdict);



    }




    public int[][]histReference(){

        TextView textViewFile = (TextView) findViewById(R.id.TextViewFileReader);

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

        //textViewFile.setText(Integer.toString(values[0][254]));

        return values;


    }


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
            message = "Cette image est celle d'un batiment";
        }
        else if (type==1){
            message= "Cette image est celle d'une végétation";
        }
        else
            message= "Désolé, cette image ne correspond à aucune de notre base d'apprentissage";

        return message;

    }
}

