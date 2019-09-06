import java.util.*;
import java.util.concurrent.RecursiveAction;

public class Classify extends RecursiveAction{

	static Vector [][][] advection; // in-plane regular grid of wind vectors, that evolve over time
	float [][][] convection; // vertical air movement strength, that evolves over time
	int [][][] classification; // cloud type per grid point, evolving over time
	static int xLow, yLow, tLow;
	static int xHigh, yHigh, tHigh;
	static final int SEQUENTIAL_CUTOFF=500;
	int dimL, dimH;

	Classify (Vector [][][] advection, float [][][] convection, int tL, int xL, int yL, int tH, int xH, int yH ){

		this.advection = advection;
		this.convection = convection;
		xLow = xL; yLow = yL; tLow = tL;
		xHigh = xH; yHigh = yH; tHigh = tH; 
		dimL = xLow * yLow * tLow;
		dimH = xHigh * yHigh * tHigh;

	}

	protected void compute(){

		if((dimH-dimL)< SEQUENTIAL_CUTOFF) {
				  
			for(int t = tLow; t < tHigh; t++)
				for(int x = xLow; x < xHigh; x++)
					for(int y = yLow; y < yHigh; y++){

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

		else {

			Classify left = new Classify(advection,convection,tLow,xLow,yLow,(tHigh+tLow)/2,(xHigh+xLow)/2, (yHigh+yLow)/2);
			Classify right = new Classify(advection,convection,(tHigh+tLow)/2,(xHigh+xLow)/2, (yHigh+yLow)/2, tHigh,xHigh,yHigh);
			
		}
	}

	public static Double localWindDirection(int t, int x, int y){

		Float xSum = Float.valueOf(0);
		Float ySum = Float.valueOf(0);

		int points = 0;

		for (int i = -1; i<2; i++){
			for (int j = -1; j<2; j++){

				if (x==0 && i==-1)
					continue;
				if (y==0 && j==-1)
					continue;
				if (x==(xHigh-xLow-1) && i==1)
					continue;
				if (y==(yHigh-yLow-1) && j==1)
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
}
