import java.util.*;


/**
* This is the main class containing the sequential code
* @author Niceta Nduku NDKNIC001
*/
public class SeqCloudData{

	static Vector [][][] advection; // in-plane regular grid of wind vectors, that evolve over time
	static float [][][] convection; // vertical air movement strength, that evolves over time
	static int [][][] classification; // cloud type per grid point, evolving over time
	static int dimx, dimy, dimt; // data dimensions
	static Vector wind;

	private static long startTime = 0;

	private static ArrayList times = new ArrayList(4);

	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}

	SeqCloudData(Vector [][][] advection, float [][][] convection, int [][][] classification,int dimx, int dimy, int dimt){

		this.advection = advection;
		this.convection = convection;
		this.classification = classification;
		this.dimt=dimt;
		this.dimx=dimx;
		this.dimy=dimy;
	}

	static void locate(int pos, int [] ind)
	{
		ind[0] = (int) pos / (dimx*dimy); // t
		ind[1] = (pos % (dimx*dimy)) / dimy; // x
		ind[2] = pos % (dimy); // y
	}

	public void prevailingWind (int dim){
		
		Float xSum = Float.valueOf(0);
		Float ySum = Float.valueOf(0);

		int[] ind = new int[3];

		for(int i=0; i < dim; i++){

				locate(i,ind);

				xSum += (Float)(advection[ind[0]][ind[1]][ind[2]].get(0));
				ySum += (Float)(advection[ind[0]][ind[1]][ind[2]].get(1));
					
		}

		wind = new Vector();
		wind.add(0, xSum/dim);
		wind.add(1, ySum/dim);

	}

	private static Double localWindDirection(int t, int x, int y){

		Float xSum = Float.valueOf(0);
		Float ySum = Float.valueOf(0);
		int neighbours=0;

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
				neighbours++;
			}
		} 

		Float xAvg = xSum/neighbours;
		Float yAvg = ySum/neighbours;

		Double mag = Math.sqrt(Math.pow(xAvg,2) +Math.pow(yAvg,2));

		return mag;
	}

	public void classification (){

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
	public static void main(String[] args){

        String inputFileName = args[0]; // file containing all data
        String seqOutputFileName = args[1]; // file name for sequential output

        Locale.setDefault(Locale.US); /// needed to use this to remove comas

    	CloudData.readData(inputFileName);
    	int [][][] classification = CloudData.classification;
    	Vector [][][] advection = CloudData.advection;
    	float [][][] convection = CloudData.convection;
    	int dimt = CloudData.dimt;
    	int dimx = CloudData.dimx;
    	int dimy = CloudData.dimy;

    	float time;// variable to be used to ge the times

    	// Sequential
    	SeqCloudData sequential = new SeqCloudData(advection,convection,classification,dimx,dimy,dimt);

    	System.gc();
		tick();
		sequential.prevailingWind(CloudData.dim());
		time = tock();
		times.add(time);

		System.gc();
		tick();
		sequential.classification();
		time = tock();
		times.add(time);

		CloudData.classification = classification;
		CloudData.writeData(seqOutputFileName, wind); // write sequential output to file

		System.out.println(times.toString());// output all times
	}
}