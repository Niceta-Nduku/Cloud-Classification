import java.util.concurrent.RecursiveTask;
import java.util.*;

/**
* This is the main application that creats a hashtable from data in a file
* searches the table and displays the search statistic  
* @author Niceta Nduku NDKNIC001
*/
public class Prevail extends RecursiveTask{

	int lo; // arguments
	int hi;
	Vector [][][] advection;
	static int dimx, dimy, dimt; // data dimensions
	static final int SEQUENTIAL_CUTOFF=100;

	/**
	* This is the main application that creats a hashtable from data in a file
	* searches the table and displays the search statistic  
	* @author Niceta Nduku NDKNIC001
	*/
	Prevail (Vector [][][] advection, int l, int h, int dimt, int dimx, int dimy){


		this.advection = advection;
		lo=l; hi=h;
		this.dimt=dimt;
		this.dimx=dimx;
		this.dimy=dimy;

	}

	/**
	* This is the main application that creats a hashtable from data in a file
	* searches the table and displays the search statistic  
	* @author Niceta Nduku NDKNIC001
	*/
	protected Float[] compute(){

		if((hi-lo)< SEQUENTIAL_CUTOFF) {

			Float xSum = Float.valueOf(0);
			Float ySum = Float.valueOf(0);

			int[] ind = new int[3];
			
			for(int i=lo; i < hi; i++){

					locate(i,ind,dimt,dimx,dimy);

					xSum += (Float)(advection[ind[0]][ind[1]][ind[2]].get(0));
					ySum += (Float)(advection[ind[0]][ind[1]][ind[2]].get(1));
					
		}

			return new Float[] {xSum,ySum};
		}

		else {

			Prevail left = new Prevail(advection, lo,(hi+lo)/2, dimt,dimx,dimy);
			Prevail right = new Prevail(advection,(hi+lo)/2,hi, dimt,dimx,dimy);
			// order of next 4 lines
			// essential – why?
			left.fork();
			Float[] rightAns = (Float[])right.compute();
			Float[] leftAns  = (Float[])left.join();

			return new Float[] {leftAns[0] + rightAns[0],leftAns[1] + rightAns[1]};
		}
	}

	/**
	* This is the main application that creats a hashtable from data in a file
	* searches the table and displays the search statistic  
	* @author Niceta Nduku NDKNIC001
	*/
	static void locate(int pos, int [] ind,int dimt,int dimx,int dimy)
	{
		ind[0] = (int) pos / (dimx*dimy); // t
		ind[1] = (pos % (dimx*dimy)) / dimy; // x
		ind[2] = pos % (dimy); // y
	}


}