package com.example.marly.lbpapp;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.Vector;

import static java.lang.Math.abs;

import static java.lang.Math.pow;
/**
 * Created by Marly on 24/10/2017.
 * Classe qui calcule une image LBP
 */


public class LBP {

    private Mat imageLbp = new Mat();


    public LBP() {
        this.imageLbp = new Mat(0,0, CvType.CV_8U);
    }

    public void setImageLbp(Mat imageSrc){
         this.imageLbp = new Mat(imageSrc.rows(),imageSrc.cols(), CvType.CV_8U);


        for(int i=1; i< imageSrc.size().height-1; i++){
            for(int j=1; j<imageSrc.size().width-1; j++){
                double[] val_centre= imageSrc.get(i,j);
                Vector nbinaire = new Vector();



                for(int k=0; k<imageSrc.channels();k++){
                    if(imageSrc.get(i-1,j-1)[k]<=val_centre[k]){
                        nbinaire.add(1);

                    }
                    else {
                        nbinaire.add(0);

                    }

                    if(imageSrc.get(i-1,j)[k]<=val_centre[k]){
                        nbinaire.add(1);

                    }
                    else {
                        nbinaire.add(0);

                    }

                    if(imageSrc.get(i-1,j+1)[k]<=val_centre[k]){
                        nbinaire.add(1);

                    }
                    else {
                        nbinaire.add(0);

                    }

                    if(imageSrc.get(i,j+1)[k]<=val_centre[k]){
                        nbinaire.add(1);

                    }
                    else {
                        nbinaire.add(0);

                    }

                    if(imageSrc.get(i+1,j+1)[k]<=val_centre[k]){
                        nbinaire.add(1);
                    }
                    else {
                        nbinaire.add(0);
                    }

                    if(imageSrc.get(i+1,j)[k]<=val_centre[k]){
                        nbinaire.add(1);
                    }
                    else {
                        nbinaire.add(0);
                    }

                    if(imageSrc.get(i+1,j-1)[k]<=val_centre[k]){
                        nbinaire.add(1);
                    }
                    else {
                        nbinaire.add(0);
                    }

                    if(imageSrc.get(i,j-1)[k]<=val_centre[k]){
                        nbinaire.add(1);
                    }
                    else {
                        nbinaire.add(0);
                    }


                    this.imageLbp.put(i,j,BinaryToDecimal(nbinaire));



                }
            }
        }



    }


    public Mat getImageLbp(){
        return imageLbp;
    }



    public double BinaryToDecimal(Vector vec){

        double result = 0.0;

        for(int i =0; i<vec.size();i++){
            result = result + (Double.valueOf((vec.elementAt(i).toString()))*pow(2, (abs(i-7))));
        }


        return result;
    }
}
