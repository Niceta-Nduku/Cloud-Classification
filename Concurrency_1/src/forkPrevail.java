import java.util.concurrent.RecursiveTask;

public class Prevail extends RecursiveATask{
		Float xSum = Float.valueOf(0);
		Float ySum = Float.valueOf(0);
		int xLow, yLow, tLow;
		int xHigh, yHigh, tHigh;
		Vector [][][] advection;
		static final int SEQUENTIAL_CUTOFF=500;
		int dimL, dimH;

	Prevail (Vector [][][] advection, int tL, int xL, int yL, int tH, int xH, int yH ){

		this.advection = advection;
		xLow = xL; yLow = yL; tLow = tL;
		xHigh = xH; x=yHigh = yH; tHigh = tH;  

		dimL = xLow * yLow * tLow;
		dimH = xHigh * yHigh * tHigh;

	}

	protected Vector compute(){

		if((dimH-dimL)< SEQUENTIAL_CUTOFF) {
				  
			for(int t = tLow; t < tHigh; t++)
				for(int t = tLow; t < tHigh; x++)
					for(int t = tLow; t < tHigh; y++){

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
			Vector leftAns  = left.join();

			Vector wind = new Vector();

			wind.add(0,rightAns.get(0)+leftAns.get(0));
			wind.add(1,rightAns.get(1)+leftAns.get(1));

			return wind;
	}
}