/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency_1;

/**
 *
 * @author myneighbour
 */

import java.util.*;

public class Concurrency_1 {

	private Vector wind;
	int [][][] classification;
	static 
	int dimx, dimy, dimt;

	public void prevailingWind (Vector [][][] advection, int dim){

		
		Float xSum = Float.valueOf(0);
		Float ySum = Float.valueOf(0);

		for(int t = 0; t < dimt; t++)
				for(int x = 0; x < dimx; x++)
					for(int y = 0; y < dimy; y++){
						 xSum += (Float) advection[t][x][y].get(0);
						 ySum += (Float) advection[t][x][y].get(1);
		}

		wind = new Vector();
		wind.add(0, xSum.floatValue()/dim);
		wind.add(0, ySum.floatValue()/dim);
		
	}

	public void classification (Vector [][][] advection, float [][][] convection){

		for(int t = 0; t < dimt; t++)
				for(int x = 0; x < dimx; x++)
					for(int y = 0; y < dimy; y++){
						 int mag = Math.sqrt(advection[t][x][y].get(0)*advection[t][x][y].get(1));
						 float absConvec = Math.abs(convection[t][x][y]);

						 if (absConvec > mag)
						 	classification [t][x][y] = 0;
						 else if (mag>0.2 && mag >= absConvec)
						 	classification [t][x][y] = 1;
						 else
						 	classification [t][x][y] = 2;
		}
	}

	 /**
     * @param args the command line arguments
     */

    public static void main(String[] args){
        // TODO code application logic here

        String inputFileName = args[0];
        String outputFileName = args[1];

    	CloudData cloud_data = new CloudData();

    	cloud_data.readData(inputFileName);

    	dimx = cloud_data.dimx;
    	dimy = cloud_data.dimy;
    	dimt = cloud_data.dimt;


    	prevailingWind(cloud_data.advection, cloud_data.dim());

    	classification(cloud_data.advection,cloud_data.convection);

    	cloud_data.classification = classification;

    	cloud_data.writeData(outputFileName, wind);

    }
    
}
