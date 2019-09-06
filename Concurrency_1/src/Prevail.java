import java.util.concurrent.RecursiveTask;
import java.util.*;

public class Prevail extends RecursiveTask{

	int xLow, yLow, tLow;
	int xHigh, yHigh, tHigh;
	Vector [][][] advection;
	static final int SEQUENTIAL_CUTOFF=500;
	int dimL, dimH;

	Prevail (Vector [][][] advection, int tL, int xL, int yL, int tH, int xH, int yH ){

		this.advection = advection;
		xLow = xL; yLow = yL; tLow = tL;
		xHigh = xH; yHigh = yH; tHigh = tH;  

		dimL = xLow * yLow * tLow;
		dimH = xHigh * yHigh * tHigh;

	}

	protected Vector compute(){

		if((dimH-dimL)< SEQUENTIAL_CUTOFF) {
			Float xSum = Float.valueOf(0);
			Float ySum = Float.valueOf(0);
			
			for(int t = tLow; t < tHigh; t++)
				for(int x = xLow; x < xHigh; x++)
					for(int y = yLow; y < yHigh; y++){

						xSum += (Float)(advection[t][x][y].get(0));
						ySum += (Float)(advection[t][x][y].get(1));
						
			}


			Vector wind = new Vector();

			wind.add(0, xSum.floatValue()/(dimH-dimL));
			wind.add(1, ySum.floatValue()/(dimH-dimL));

			return wind;
		}

		else {

			Prevail left = new Prevail(advection,tLow,xLow,yLow,(tHigh+tLow)/2,(xHigh+xLow)/2, (yHigh+yLow)/2);
			Prevail right = new Prevail(advection,(tHigh+tLow)/2,(xHigh+xLow)/2, (yHigh+yLow)/2, tHigh,xHigh,yHigh);
			// order of next 4 lines
			// essential – why?
			left.fork();
			Vector rightAns = right.compute();
			Vector leftAns  = (Vector)left.join();

			Vector wind = new Vector();

			wind.add(0,(Float)rightAns.get(0)+(Float)leftAns.get(0));
			wind.add(1,(Float)rightAns.get(1)+(Float)leftAns.get(1));

			return wind;
	}
}
}