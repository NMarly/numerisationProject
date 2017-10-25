package com.example.marly.lbpapp;

import org.opencv.core.Mat;

/**
 * Created by Marly on 25/10/2017.
 */

public class Histogramme {

    private int[] tab;


    public Histogramme(){

        tab = new int[255];

        for(int i=0;i<255;i++){
            tab[i]=0;
        }

    }


    public void setTab(Mat imageLbp){

        for(int i=0; i<255; i++){
            for(int j=0; j<imageLbp.size().width;j++){
                for(int k=0; k<imageLbp.size().height;k++){
                    for(int l=0; l<imageLbp.channels();l++){



                        if(imageLbp.get(k,j)[l] == i){

                            this.tab[i]=tab[i]+1;

                        }
                    }



                }
            }
        }



    }

    public int[] getTab(){
        return tab;
    }
}
