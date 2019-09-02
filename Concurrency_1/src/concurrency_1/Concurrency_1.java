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

public class Concurrency_1 {

	Vector [][] wind;
	int [][][] classification;

	Vector prevailingWind (Vector advection){

		for(int t = 0; t < dimt; t++)
				for(int x = 0; x < dimx; x++)
					for(int y = 0; y < dimy; y++){
						 xSum += advection[t][x][y].x;
						 ySum += advection[t][x][y].y;
		}

		wind = new Vector();
		wind.x = xSum/dim;
		wind.y = ySum/dim;

		return wind;

	}

	int clasification (Vector [][][] advection, float [][][] convection, int [][][] classification){

		this.classification = classification;

		for(int t = 0; t < dimt; t++)
				for(int x = 0; x < dimx; x++)
					for(int y = 0; y < dimy; y++){
						 int mag = Math.sqrt(advection[t][x][y].x*advection[t][x][y].y);
						 float absConvec = Math.abs(convection[t][x][y]);

						 if (absConvec > mag)
						 	classification [t][x][y] = 0;
						 else if (mag>0.2 && mag >= absConvec)
						 	classification [t][x][y] = 1;
						 else
						 	classification [t][x][y] = 2;
		}

		return classification;

	}

	 /**
     * @param args the command line arguments
     */

    public static void main(String[] args){
        // TODO code application logic here


    }
    
}
