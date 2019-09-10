import java.util.concurrent.RecursiveTask;
import java.util.*;

public class Prevail extends RecursiveTask{

	int lo; // arguments
	int hi;
	Vector [][][] advection;
	static final int SEQUENTIAL_CUTOFF=300000;
	CloudData cloudD;

	Prevail (Vector [][][] advection, CloudData d, int l, int h){

		this.advection = advection;
		cloudD = d;
		lo=l; hi=h;

	}

	protected Float[] compute(){

		if((hi-lo)< SEQUENTIAL_CUTOFF) {

			Float xSum = Float.valueOf(0);
			Float ySum = Float.valueOf(0);

			int[] ind = new int[3];
			
			for(int i=lo; i < hi; i++){

					cloudD.locate(i,ind);

					xSum += (Float)(advection[ind[0]][ind[1]][ind[2]].get(0));
					ySum += (Float)(advection[ind[0]][ind[1]][ind[2]].get(1));
					
		}

			return new Float[] {xSum,ySum};
		}

		else {

			Prevail left = new Prevail(advection,cloudD, lo,(hi+lo)/2);
			Prevail right = new Prevail(advection,cloudD,(hi+lo)/2,hi);
			// order of next 4 lines
			// essential – why?
			left.fork();
			Float[] rightAns = (Float[])right.compute();
			Float[] leftAns  = (Float[])left.join();

			return new Float[] {leftAns[0] + rightAns[0],leftAns[1] + rightAns[1]};
	}
}
}