import java.util.*;
public class SeqCloudData{

	static Vector [][][] advection; // in-plane regular grid of wind vectors, that evolve over time
	static float [][][] convection; // vertical air movement strength, that evolves over time
	static int [][][] classification; // cloud type per grid point, evolving over time
	static int dimx, dimy, dimt; // data dimensions
	static Vector wind;

	SeqCloudData(Vector [][][] advection, float [][][] convection, int [][][] classification,int dimx, int dimy, int dimt){

		this.advection = advection;
		this.convection = convection;
		this.classification = classification;
		this.dimt=dimt;
		this.dimx=dimx;
		this.dimy=dimy;
	}

	public static void prevailingWind (int dim){
		
		Float xSum = Float.valueOf(0);
		Float ySum = Float.valueOf(0);

		for(int t = 0; t < dimt; t++)
			for(int x = 0; x < dimx; x++)
				for(int y = 0; y < dimy; y++){

					xSum += (Float)(advection[t][x][y].get(0));
					ySum += (Float)(advection[t][x][y].get(1));
					
		}

		System.out.println(xSum+","+ySum);

		wind = new Vector();
		wind.add(0, xSum/dim);
		wind.add(1, ySum/dim);

	}

	public static Double localWindDirection(int t, int x, int y){

		Float xSum = Float.valueOf(0);
		Float ySum = Float.valueOf(0);

		for (int i = -1; i<2; i++){
			for (int j = -1; j<2; j++){

				if (x==0 && i==-1)
					continue;
				if (y==0 && j==-1)
					continue;
				if (x==dimx-1 && i==1)
					continue;
				if (y==dimy-1 && j==1)
					continue;	

				xSum += (Float)advection[t][x+i][y+j].get(0);
				ySum += (Float)advection[t][x+i][y+j].get(1);
			}
		} 

		Float xAvg = xSum/9;
		Float yAvg = ySum/9;

		Double mag = Math.sqrt(Math.pow(xAvg,2) +Math.pow(yAvg,2));

		return mag;
	}

	public static void classification (){

		for(int t = 0; t < dimt; t++)
				for(int x = 0; x < dimx; x++)
					for(int y = 0; y < dimy; y++){

						 Double mag = localWindDirection(t,x,y);
						 Float absConvec = Math.abs(convection[t][x][y]);

						 if (absConvec > mag)
						 	classification [t][x][y] = 0;
						 else if ((mag>0.2) && (mag >= absConvec))
						 	classification [t][x][y] = 1;
						 else
						 	classification [t][x][y] = 2;
		}

	}
}