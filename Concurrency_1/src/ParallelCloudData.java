import java.util.*;

import java.util.concurrent.ForkJoinPool;
/**
* This is the main class for the ParallelCloud data
* @author Niceta Nduku NDKNIC001
*/
public class ParallelCloudData{

	static Vector [][][] advection; // in-plane regular grid of wind vectors, that evolve over time
	static float [][][] convection; // vertical air movement strength, that evolves over time
	static int [][][] classification; // cloud type per grid point, evolving over time
	static int dimx, dimy, dimt; // data dimensions
	static Vector wind;

	static final ForkJoinPool prevailFJPool = new ForkJoinPool();
	static final ForkJoinPool classifyFJPool = new ForkJoinPool();

	private static long startTime = 0;

	private static ArrayList times = new ArrayList(4);

	/**
	* 
	* @author Prof James Bain
	*/
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	
	/**
	* 
	* @author Prof James Bain
	*/
	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}
	
	static void prevailingWind(int dim){
		
	  	Float[] sum = (Float[])prevailFJPool.invoke(new Prevail(advection,0,dim,dimt,dimx,dimy));
	  	
	  	wind = new Vector();
		wind.add(0,sum[0]/dim);
		wind.add(1,sum[1]/dim);

	}

	static void classification(int dim){
		Classify c = new Classify(advection,convection,classification,0,dim,dimt,dimx,dimy);

		classifyFJPool.invoke(c);
		CloudData.classification = c.classification;
	}

	public static void main(String[] args){

        String inputFileName = args[0]; // file containing all data
        String seqOutputFileName = args[1]; // file name for sequential output

        Locale.setDefault(Locale.US); /// needed to use this to remove comas

    	CloudData.readData(inputFileName);
    	classification = CloudData.classification;
    	advection = CloudData.advection;
    	convection = CloudData.convection;
    	dimt = CloudData.dimt;
    	dimx = CloudData.dimx;
    	dimy = CloudData.dimy;

    	float time;// variable to be used to ge the times

		// Parallel
		System.gc();
		tick();
		prevailingWind(CloudData.dim());
		time = tock();
		times.add(time);

		System.gc();
		tick();
		classification(CloudData.dim());
		time = tock();
		times.add(time);

		CloudData.writeData(seqOutputFileName, wind); // write sequential output to file

		System.out.println(times.toString());// output all times
	}
}