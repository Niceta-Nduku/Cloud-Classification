import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

import java.util.concurrent.ForkJoinPool;

public class CloudData {

	static Vector [][][] advection; // in-plane regular grid of wind vectors, that evolve over time
	static float [][][] convection; // vertical air movement strength, that evolves over time
	static int [][][] classification; // cloud type per grid point, evolving over time
	static int dimx, dimy, dimt; // data dimensions
	private static Vector wind;
	static long startTime = 0;

	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}

	// overall number of elements in the timeline grids
	static int dim(){
		return dimt*dimx*dimy;
	}

	// convert linear position into 3D location in simulation grid
	void locate(int pos, int [] ind)
	{
		ind[0] = (int) pos / (dimx*dimy); // t
		ind[1] = (pos % (dimx*dimy)) / dimy; // x
		ind[2] = pos % (dimy); // y
	}


	// read cloud simulation data from file
	static void readData(String fileName) {
		try{
			Scanner sc = new Scanner(new File(fileName), "UTF-8");

			// input grid dimensions and simulation duration in timesteps
			dimt = sc.nextInt();
			dimx = sc.nextInt();
			dimy = sc.nextInt();


			// initialize and load advection (wind direction and strength) and convection
			advection = new Vector[dimt][dimx][dimy];
			convection = new float[dimt][dimx][dimy];
			for(int t = 0; t < dimt; t++)
				for(int x = 0; x < dimx; x++)
					for(int y = 0; y < dimy; y++){
						
						advection[t][x][y] = new Vector();
						advection[t][x][y].add(Float.parseFloat(sc.next()));
						advection[t][x][y].add(Float.parseFloat(sc.next()));
						convection[t][x][y] = Float.parseFloat(sc.next());
					}

			classification = new int[dimt][dimx][dimy];
			sc.close();
		}
		catch (IOException e){
			System.out.println("Unable to open input file "+fileName);
			e.printStackTrace();
		}
		catch (java.util.InputMismatchException e){
			System.out.println("Malformed input file "+fileName);
			e.printStackTrace();
		}
	}

	// write classification output to file
	static void writeData(String fileName, Vector wind){
		 try{
			 FileWriter fileWriter = new FileWriter(fileName);
			 PrintWriter printWriter = new PrintWriter(fileWriter);
			 printWriter.printf("%d %d %d\n", dimt, dimx, dimy);
			 printWriter.printf("%f %f\n", wind.get(0), wind.get(1));

			 for(int t = 0; t < dimt; t++){
				 for(int x = 0; x < dimx; x++){
					for(int y = 0; y < dimy; y++){
						printWriter.printf("%d ", classification[t][x][y]);
					}
				 }
				 printWriter.printf("\n");
		     }
			 printWriter.close();
		 }
		 catch (IOException e){
			 System.out.println("Unable to open output file "+fileName);
				e.printStackTrace();
		 }
	}

	static final ForkJoinPool prevailFJPool = new ForkJoinPool();
	static final ForkJoinPool classifyFJPool = new ForkJoinPool();
	
	static void prevailingWind(){
		
	  	Float xSum = (Float)prevailFJPool.invoke(new PrevailX(advection,0,0,0,dimt,dimx,dimy));
	  	Float ySum = (Float)prevailFJPool.invoke(new PrevailY(advection,0,0,0,dimt,dimx,dimy));

	  	System.out.println(xSum+","+ySum);
		wind.set(0,ySum/dim());
		wind.set(1,ySum/dim());

	}

	static void classification(){
		Classify c = new Classify(advection,convection,classification,0,0,0,dimt,dimx,dimy);

		classifyFJPool.invoke(c);
		classification = c.classification;
	}	

	 /**
     * @param args the command line arguments
     */

    public static void main(String[] args){
        // TODO code application logic here

        String inputFileName = args[0];
        String seqOutputFileName = args[1];
        String parOutputFileName = args[2];

        Locale.setDefault(Locale.US);

    	readData(inputFileName);

    	float time;

    	System.out.println("Starting sequential");

    	SeqCloudData sequential = new SeqCloudData(advection,convection,classification,dimx,dimy,dimt);

		tick();
		sequential.prevailingWind(dim());
		wind=sequential.wind;
		time = tock();
		System.out.println(time);
		//System.out.println(wind.toString());

		tick();
		sequential.classification();
		classification = sequential.classification;
		time = tock();
		System.out.println(time);

		writeData(seqOutputFileName, wind);

		System.out.println("Starting Parallel");

		tick();
		prevailingWind();
		time = tock();
		System.out.println(time);
		//System.out.println(wind.toString());

		tick();
		classification();
		time = tock();
		System.out.println(time);
    	
    	writeData(parOutputFileName, wind);
    }
}
