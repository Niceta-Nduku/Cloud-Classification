import java.util.*;
import java.util.concurrent.RecursiveAction;


/**
* This is the class that extends RecursiveAction for the parallel classification of the cloud data
* @author Niceta Nduku NDKNIC001
*/
public class Classify extends RecursiveAction{

	static Vector [][][] advection; // in-plane regular grid of wind vectors, that evolve over time
	float [][][] convection; // vertical air movement strength, that evolves over time
	int [][][] classification; // cloud type per grid point, evolving over time
	int lo; // arguments
	int hi;
	static int dimx, dimy, dimt; // data dimensions
	static final int SEQUENTIAL_CUTOFF=100000;

	
	Classify (Vector [][][] advection, float [][][] convection, int [][][] classification, int l, int h, int dimt, int dimx, int dimy){

		this.advection = advection;
		this.convection = convection;
		this.classification = classification;
		lo=l; hi=h;
		this.dimt=dimt;
		this.dimx=dimx;
		this.dimy=dimy;

	}

	protected void compute(){


		if((hi-lo)< SEQUENTIAL_CUTOFF) {

			Float xSum = Float.valueOf(0);
			Float ySum = Float.valueOf(0);

			int[] ind = new int[3];
			
			for(int i=lo; i < hi; i++){

				locate(i,ind,dimt,dimx,dimy);

				Double mag = localWindDirection(ind[0],ind[1],ind[2]);
				Float absConvec = Math.abs(convection[ind[0]][ind[1]][ind[2]]);

				if (absConvec > mag)
					classification [ind[0]][ind[1]][ind[2]] = 0;
				else if ((mag>0.2) && (mag >= absConvec))
					classification [ind[0]][ind[1]][ind[2]] = 1;
				else
					classification [ind[0]][ind[1]][ind[2]] = 2;
			}
		}

		else {

			Classify left = new Classify(advection,convection,classification,lo,(hi+lo)/2, dimt,dimx,dimy);
			Classify right = new Classify(advection,convection,classification,(hi+lo)/2,hi, dimt,dimx,dimy);
			left.fork();
			right.compute();
			left.join();
		}
	}


	public static Double localWindDirection(int t, int x, int y){

		Float xSum = Float.valueOf(0);
		Float ySum = Float.valueOf(0);

		int neighbours = 0;

		for (int i = -1; i<2; i++){
			for (int j = -1; j<2; j++){

				if (x==0 && i==-1)
					continue;
				if (y==0 && j==-1)
					continue;
				if (x==(dimx-1) && i==1)
					continue;
				if (y==(dimy-1) && j==1)
					continue;	

				xSum += (Float)advection[t][x+i][y+j].get(0);
				ySum += (Float)advection[t][x+i][y+j].get(1);
				neighbours++;
			}
		} 

		Float xAvg = xSum/neighbours;
		Float yAvg = ySum/neighbours;

		Double mag = Math.sqrt(Math.pow(xAvg,2) +Math.pow(yAvg,2));

		return mag;
	}

	static void locate(int pos, int [] ind,int dimt,int dimx,int dimy)
	{
		ind[0] = (int) pos / (dimx*dimy); // t
		ind[1] = (pos % (dimx*dimy)) / dimy; // x
		ind[2] = pos % (dimy); // y
	}
}
