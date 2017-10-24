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

import java.io.IOException;
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

        Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.imagetest);

        Utils.bitmapToMat(image,imageOpencv);
        Imgproc.cvtColor(imageOpencv, imageOpencv, Imgproc.COLOR_BGR2GRAY);

        Bitmap imgray = Bitmap.createBitmap(imageOpencv.width(),imageOpencv.height(), Bitmap.Config.ARGB_8888);
        Bitmap lbpBitmap = Bitmap.createBitmap(imageOpencv.width(),imageOpencv.height(), Bitmap.Config.ARGB_8888);


        Utils.matToBitmap(imageOpencv,imgray);

        LBP imLbp = new LBP();



        Utils.matToBitmap(imLbp.setMat(imageOpencv),lbpBitmap);




        ImageView vue = (ImageView)findViewById(R.id.vue);
        //TextView text = (TextView) findViewById(R.id.sample_text);

        //text.setText(Double.toString(pixel));
        vue.setImageBitmap(lbpBitmap);

    }
}

