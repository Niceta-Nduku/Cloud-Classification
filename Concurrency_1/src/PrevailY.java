import java.util.concurrent.RecursiveTask;
import java.util.*;

public class PrevailY extends RecursiveTask{

	int xLow, yLow, tLow;
	int xHigh, yHigh, tHigh;
	Vector [][][] advection;
	static final int SEQUENTIAL_CUTOFF=300000;
	int dimL, dimH;

	Float windSum = Float.valueOf(0); //sum

	PrevailY (Vector [][][] advection, int tL, int xL, int yL, int tH, int xH, int yH ){

		this.advection = advection;
		xLow = xL; yLow = yL; tLow = tL;
		xHigh = xH; yHigh = yH; tHigh = tH;  

		dimL = xLow * yLow * tLow;
		dimH = xHigh * yHigh * tHigh;

	}

	protected Float compute(){

		if((dimH-dimL)< SEQUENTIAL_CUTOFF) {
			
			for(int t = tLow; t < tHigh; t++)
				for(int x = xLow; x < xHigh; x++)
					for(int y = yLow; y < yHigh; y++){

					//windSum[0] += (Float)(advection[t][x][y].get(0));
					windSum += (Float)(advection[t][x][y].get(1));
					
		}

			return windSum;
		}

		else {

			PrevailY left = new PrevailY(advection,tLow,xLow,yLow,(tHigh+tLow)/2,(xHigh+xLow)/2, (yHigh+yLow)/2);
			PrevailY right = new PrevailY(advection,(tHigh+tLow)/2,(xHigh+xLow)/2, (yHigh+yLow)/2, tHigh,xHigh,yHigh);
			// order of next 4 lines
			// essential – why?
			left.fork();
			Float rightAns = (Float)right.compute();
			Float leftAns  = (Float)left.join();

			return leftAns + rightAns;
	}
}
}