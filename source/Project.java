/*
 * Project.java
 *
 * This file implements the APPROX MST, Local Search 2 opt with annealing algorithms
 *
 * The File processes one file of inputs at a time and generates the out files in the
 *  current directory
 *
 *  The usage of this file is as follows
 *   Usage: 
 *    java Project <file_name_with_full_absolute_path> <cutofftime_seconds> <method[mstapprox|heur_furthest_ins|ls_2opt_k5|ls_2opt_kall|ls_2opt_sa_k5|ls_2opt_sa_kall]> [<random_seed_for_local_search_algorithms_type_long>] [<runId-for local search algorithms>]
*/
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.ListIterator;
import java.util.Random; 
import java.lang.Math; 
import java.nio.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.FileOutputStream;
import java.lang.Exception;

public class Project{

	private static final double nanosecs = 1000000000;
	private static final double PI = 3.141592;
	private static final double earthRadius = 6378.388;
	/*
	 * variables for a given file run
	 */
	private static File  fileNameWithAbsolutePath;
	private static Double  cutoffTimeSeconds;
	private static String  method;
	private static String  name;
	private static Integer  dimension = -1;
	private static String  edgeWeightType;
	private static Double  optimalCost;
	private static double  mstValue = 0;
	private static boolean  timedOut = false;
	/* nested graph to store the mst */
	/* source_node->dest_node->weight*/
	private static HashMap<Integer,HashMap<Integer,Double>> sourceGTree = new HashMap<Integer,HashMap<Integer,Double>>();    
	private static int runId = 1;
	private static long    randomSeed = 0;
	private static Random  generator;


        /* 
	 *
	 * parse the data file contents to a nested hash map - which is a matrix of costs for every edge
	 */
	private static void parseEdges(File fileNameWithPath) throws Exception{

		try {

                 BufferedReader in = new BufferedReader(new FileReader(fileNameWithPath));

		 /*
		  * Read the first five lines from file as follows
		  *
		  * NAME: berlin52
		  * COMMENT: 52 locations in Berlin (Groetschel)
		  * DIMENSION: 52
		  * EDGE_WEIGHT_TYPE: EUC_2D
		  * OPTIMAL_COST: 7542
		  * NODE_COORD_SECTION
		  */ 
		 for (int i=0;i<6;i++){
                 	String str = in.readLine().trim();
			String[] strArray = str.split(":\\s+");
			if (strArray[0].equals("NAME")) {
				Project.name = strArray[1]; 
			} else if (strArray[0].equals("DIMENSION")) {
				Project.dimension = new Integer(Integer.parseInt(strArray[1]));
			} else if (strArray[0].equals("OPTIMAL_COST")) {
				Project.optimalCost = new Double(Double.parseDouble(strArray[1])); 
			} else if (strArray[0].equals("EDGE_WEIGHT_TYPE")) {
				Project.edgeWeightType = strArray[1]; }
		 }

		 if ( dimension == -1 ){
			 throw new Exception("ERROR:Failed to read the file contents correctly from "+fileNameWithPath.getPath());
		 }

		 /* read each vertex and its coordinates */
		 Integer[] vertices = new Integer[Project.dimension];
		 Double[] xCord = new Double[Project.dimension];
		 Double[] yCord = new Double[Project.dimension];
		 int vertexCount = 0;

		 String str;
                 while ((str = in.readLine().trim()) != null) {
			if (str.equals("EOF"))
				break;

		   	String[] ar=str.split("\\s+");

			vertices[vertexCount] =new Integer(Integer.parseInt(ar[0]));
			xCord[vertexCount] =new Double(Double.parseDouble(ar[1]));
			yCord[vertexCount] =new Double(Double.parseDouble(ar[2]));

			vertexCount++;
		}

                in.close();

		/* 
		  * Generate the cost matrix between each pair of vertices
		  *  as a java HashMap<Integer,HashMap<Integer,Double>>
		*/
		for (int i=0;i<Project.dimension;i++) {
			int vi = vertices[i].intValue();
			for (int j=i+1;j<Project.dimension;j++) {

				int vj = vertices[j].intValue();
				double cost_ij = 0;
				if ( vi != vj)
				{

					if (Project.edgeWeightType.equals("GEO")){
						int deg;
						double min;
	
	  					deg = (int)(xCord[i].doubleValue());
	  					min = xCord[i]- deg; 
	  					double latitude_i = PI * (deg + 5.0 * min/ 3.0) / 180.0; 
	
	  					deg = (int)(yCord[i].doubleValue());
	  					min = yCord[i]- deg; 
	  					double longitude_i = PI * (deg + 5.0 * min/ 3.0) / 180.0; 
	
	  					deg = (int)(xCord[j].doubleValue());
	  					min = xCord[j]- deg; 
	  					double latitude_j = PI * (deg + 5.0 * min/ 3.0) / 180.0; 
	
	  					deg = (int)(yCord[j].doubleValue());
	  					min = yCord[j]- deg; 
	  					double longitude_j = PI * (deg + 5.0 * min/ 3.0) / 180.0; 
	
	 					double q1 = Math.cos( longitude_i - longitude_j ); 
	 					double q2 = Math.cos( latitude_i - latitude_j ); 
	 					double q3 = Math.cos( latitude_i + latitude_j ); 

	 					cost_ij = Math.floor( earthRadius * Math.acos( 0.5*((1.0+q1)*q2 - (1.0-q1)*q3) ) + 1.0);

					} else if (Project.edgeWeightType.equals("EUC_2D")){
						double xd = xCord[i]-xCord[j];
						double yd = yCord[i]-yCord[j];
						
						//cost_ij = new Double(Math.sqrt( (xd*xd) + (yd*yd))).intValue();
						cost_ij = Math.round(Math.sqrt( (xd*xd) + (yd*yd)));
					} else {
						throw new Exception("ERROR:EDGE_WEIGHT_TYPE of GEO and EUC_WD are implemented , not implemented "+Project.edgeWeightType);
					}

				}


		   		if (!sourceGTree.containsKey(vi)){
		    			sourceGTree.put(vi,new HashMap<Integer,Double>());
		   		}

		   		if (!sourceGTree.containsKey(vj)){
		    			sourceGTree.put(vj,new HashMap<Integer,Double>());
		   		}

		   		sourceGTree.get(vi).put(vj,cost_ij);
		   		sourceGTree.get(vj).put(vi,cost_ij);

			}
		}

                } catch (IOException e) {
                  System.out.println("ERROR: Failed reading file " + fileNameWithPath.getPath());
		  throw e;
                }

	}


	/*
	 * Invoke the approx mst to get the TSP
	 */
	private static LinkedList<Integer> mstApprox(File fileNameWithPath) throws Exception{
		
		String traceString = "";
		double bestCost = Double.POSITIVE_INFINITY;
		LinkedList<Integer> bestTspList = null;
		double bestCycleTime = Double.POSITIVE_INFINITY;

		/* 
		 * read the file and build the cost table
		 */
                parseEdges(fileNameWithPath);

                /*
		 * time starts once you read the data
                 */
		double baseTime = System.nanoTime();

		/*
		 * time starts now
		 */
		while ( System.nanoTime()-baseTime < Project.cutoffTimeSeconds*nanosecs )
		{
			double cycleStartTime = System.nanoTime();
	
			/*
		 	* invoking prim algorithm to generate the MST
		 	*/
			long startNodeSeed = (long)(1000.0*generator.nextDouble());
		   	HashMap<Integer,LinkedList<Integer>>  MST = PrimsMST.generateMST(Project.sourceGTree,startNodeSeed);
			LinkedList<Integer> tspList = new LinkedList<Integer>();
	
			/*
			 * go over the mst vertices for a depth first search and get the preorder of vertices
			 *   this is the tsp
			 */
			Stack<Integer> nodeStack = new Stack<Integer>();

			/* initialized the stack with the start node of the mstree */
			nodeStack.push(MST.get(0).getFirst());

			/* use the stack to keep preorder list of vertices from mst for tsp */
			while ( !nodeStack.empty() ) {

				Integer vertex1 = nodeStack.pop();

				/* preorder list of vertices saved for tsp */
				if (!tspList.contains(vertex1)) {
					tspList.add(vertex1);
				}

				/* there are edges out of this vertex */
				if (MST.containsKey(vertex1)) {

					ListIterator<Integer> vertices = MST.get(vertex1).listIterator();

					while (vertices.hasNext()) {

						Integer vertex2 = vertices.next();
						nodeStack.push(vertex2);
						Project.mstValue += sourceGTree.get(vertex1).get(vertex2);

					}
				}
			}

			/* add the path back from last node to first node */
			tspList.addLast(tspList.getFirst());

			double tspCost = tspCost(tspList);
			//Subtract the start time from the finish time to get the actual algorithm running time; divide by 10e6 to convert to milliseconds
			double cycleTime = (System.nanoTime()-cycleStartTime);

			bestCost = tspCost;
			bestTspList = tspList;
			bestCycleTime = cycleTime;
			
			traceString = String.format("%.2f, %d",Math.round(((System.nanoTime()-baseTime)/nanosecs)*100.0)/100.0,Math.round(bestCost));

			break;
		}

		/* print the sol file */
		printSol(runId,bestTspList,bestCost);

		/* print the trace file */
		printTrace(runId,traceString);

		/* print the tab results file */
		appendTabResults(runId,bestCycleTime,bestCost,bestTspList);

		return bestTspList;

	}


	/*
	 * Invoke the greedy furthest insertion heuristic to get the TSP
	 */
	private static LinkedList<Integer> heurFurthestInsertion(File fileNameWithPath) throws Exception{
		
		String traceString = "";
		double bestCost = Double.POSITIVE_INFINITY;
		LinkedList<Integer> bestTspList = null;
		double bestCycleTime = Double.POSITIVE_INFINITY;

		/* 
		 * read the file and build the cost table
		 */
                parseEdges(fileNameWithPath);

                /*
		 * time starts once you read the data
                 */
		double baseTime = System.nanoTime();

		/*
		 * time starts now
		 */
		while ( System.nanoTime()-baseTime < Project.cutoffTimeSeconds*nanosecs )
		{
			double cycleStartTime = System.nanoTime();
	
			/*
		 	* invoking furthest insertion algorithm to get the tsp
		 	*/
			long startNodeSeed = (long)(1000.0*generator.nextDouble());
		 	LinkedList<Integer> tspList = FurthestInsertion.generateTSP(Project.sourceGTree,startNodeSeed);
	
			double tspCost = tspCost(tspList);
			//Subtract the start time from the finish time to get the actual algorithm running time; divide by 10e6 to convert to milliseconds
			double cycleTime = (System.nanoTime()-cycleStartTime);

			bestCost = tspCost;
			bestTspList = tspList;
			bestCycleTime = cycleTime;
			
			traceString = String.format("%.2f, %d",Math.round(((System.nanoTime()-baseTime)/nanosecs)*100.0)/100.0,Math.round(bestCost));

			break;
		}

		/* print the sol file */
		printSol(runId,bestTspList,bestCost);

		/* print the trace file */
		printTrace(runId,traceString);

		/* print the tab results file */
		appendTabResults(runId,bestCycleTime,bestCost,bestTspList);

		return bestTspList;

	}

	/*
	 * Invoke the local search simulated annealing algorithm with 2 opt
	 */
	private static LinkedList<Integer> localSearchSimulatedAnnealing(File fileNameWithPath, int kNeighborHood ) throws Exception{
		
		String traceString = "";
		double bestCost = Double.POSITIVE_INFINITY;
		LinkedList<Integer> bestTspList = null;
		double bestCycleTime = Double.POSITIVE_INFINITY;

		if (Project.runId == -1 )
			Project.runId = 1;

		/* 
		 * read the file and build the cost table
		 */
                parseEdges(fileNameWithPath);

                /*
		 * time starts once you read the data
                 */
		double baseTime = System.nanoTime();

		/*
		 * invoking furthest insertion algorithm to get the tsp
		 */
		long startNodeSeed = (long)(1000.0*generator.nextDouble());
		LinkedList<Integer> currentTspList = FurthestInsertion.generateTSP(Project.sourceGTree,startNodeSeed);
		
		double currentTspCost = tspCost(currentTspList);

		bestTspList = currentTspList;
		bestCost = currentTspCost;
		bestCycleTime = System.nanoTime() - baseTime;

		/* print the trace file */
		traceString = String.format("%.2f, %d",Math.round(((System.nanoTime()-baseTime)/nanosecs)*100.0)/100.0,Math.round(bestCost));
		printTrace(runId,traceString);

		/*
		 * time starts now - start annealing
		 */
		double annealingProbabilityThreshold = .2*generator.nextDouble();  // acceptance prob threshold for bad results
		double T = 100; // annealing temp
		double coolingRate = .00000001; // rate of cooling in each step

		/*
		 * remove the last node as it matches the first
		 */
		currentTspList.removeLast();

		while ( T > 1 )
		{
			/*
			 * reached cutoff time
			 */
			if ( System.nanoTime()-baseTime >= Project.cutoffTimeSeconds*nanosecs ) {
				timedOut=true;
				break;
			}

			double cycleStartTime = System.nanoTime();

			LinkedList<Integer> newTspList = currentTspList;

			/* do a 2 opt search in current k=5 neighborhood  to get a newtsp */
			/* 1. Pick the first random element in the current tsp */
			int element2 = (int)((double)(newTspList.size()-1) * generator.nextDouble());
			int element1 = element2 - 1;
			if ( element1 == -1 ){
				element1 = newTspList.size()-1;
			}
			
                        int delta;

			/*
			 * search in the neighborhood specified
                         * if not specified search all
			 */
                        if ( kNeighborHood != -1 ) {
			  /* We want to search in the specified k=n neighborhoods of element1 */
			  delta= (int)(2*kNeighborHood*generator.nextDouble()) - kNeighborHood;
                         } else {
			  delta= (int)((newTspList.size()-1)*generator.nextDouble()) - (int)(newTspList.size()/2);
			}

			if ( delta == 0 ) {
				delta = 2;
			} else if ( delta == 1 ) {
				delta = 2;
			} else if ( delta == -1) {
				delta = -2; }

			int element4 = element2 + delta;

			if ( element4 <  0 ) {
				element4 = newTspList.size()+element4;
			}else if ( element4 >=  newTspList.size() ) {
				element4 = element4-(newTspList.size()-1);
			}

			int element3 = element4 -1;
			if ( element3 == -1 ){
				element3 = newTspList.size()-1;
			}


			/*  
			 *  the new tsp will have element2->element4.......element1->element3....
			 */
			Integer vertex_3 = newTspList.get(element3);
			newTspList.set(element3,newTspList.get(element2));
			newTspList.set(element2,vertex_3);

			/*
			 * from element2+1 to element3-1 swap to reverse their order
			 */
			while ( element2 < element3 ){
				element3 = element3-1;
				if ( element3 == -1 ) {
					element3 = newTspList.size()-1;
				}

				element2 = element2 + 1;
				if ( element2 == newTspList.size() ) {
					element3 = 0;
				}

				Integer tempVertex = newTspList.get(element2);
				newTspList.set(element2,newTspList.get(element3));
				newTspList.set(element3,tempVertex);

			}

			double newTspCost = tspCost(newTspList);

			/* if new local search solution is better than eariler take the search and continue search
			 */
			if ( newTspCost <= currentTspCost ){
				currentTspList = newTspList;
				currentTspCost = newTspCost;
			} else {
			/* if new local search solution is not better than eariler 
			 *  then check the difference and the probability at this stage of annealing
			 *  if probability is higher than threshold set, then keep new search ad continue, else discard 
			 */
				if ( Math.exp((currentTspCost - newTspCost) / T) > generator.nextDouble() ){
					currentTspList = newTspList;
					currentTspCost = newTspCost;
				}
			}
	
			//Subtract the start time from the finish time to get the actual algorithm running time; divide by 10e6 to convert to milliseconds
			double cycleTime = (System.nanoTime()-cycleStartTime);

			/* first improvement , take the best each time */
			if ( newTspCost < bestCost ) {
				bestCost = newTspCost;
				bestTspList = newTspList;
				bestCycleTime = cycleTime;
				/* print the trace file */
				traceString = String.format("%.2f, %d",Math.round(((System.nanoTime()-baseTime)/nanosecs)*100.0)/100.0,Math.round(bestCost));
				appendToTraceFile(runId,traceString);

				if ( bestCost <= optimalCost )
					break;
			}

			/* the cooling */
			T = T - T * coolingRate;

		}

		/* print the sol file */
		printSol(runId,bestTspList,bestCost);

		/* print the tab results file */
		appendTabResults(runId,bestCycleTime,bestCost,bestTspList);

		return bestTspList;

	}


	/*
	 * Invoke the local search with 2 opt
	 */
	private static LinkedList<Integer> localSearch2Opt(File fileNameWithPath, int kNeighborHood ) throws Exception{
		
		String traceString = "";
		double bestCost = Double.POSITIVE_INFINITY;
		LinkedList<Integer> bestTspList = null;
		double bestCycleTime = Double.POSITIVE_INFINITY;

		if (Project.runId == -1 )
			Project.runId = 1;

		/* 
		 * read the file and build the cost table
		 */
                parseEdges(fileNameWithPath);

                /*
		 * time starts once you read the data
                 */
		double baseTime = System.nanoTime();

		/*
		 * invoking furthest insertion algorithm to get the tsp
		 */
		long startNodeSeed = (long)(1000.0*generator.nextDouble());
		LinkedList<Integer> currentTspList = FurthestInsertion.generateTSP(Project.sourceGTree,startNodeSeed);
		
		double currentTspCost = tspCost(currentTspList);

		bestTspList = currentTspList;
		bestCost = currentTspCost;
		bestCycleTime = System.nanoTime() - baseTime;

		/* print the trace file */
		traceString = String.format("%.2f, %d",Math.round(((System.nanoTime()-baseTime)/nanosecs)*100.0)/100.0,Math.round(bestCost));
		printTrace(runId,traceString);

		/*
		 * remove the last node as it matches the first
		 */
		currentTspList.removeLast();

		while ( true )
		{
			/*
			 * reached cutoff time
			 */
			if ( System.nanoTime()-baseTime >= Project.cutoffTimeSeconds*nanosecs ) {
				timedOut=true;
				break;
			}

			double cycleStartTime = System.nanoTime();

			LinkedList<Integer> newTspList = currentTspList;

			/* do a 2 opt search in current k=5 neighborhood  to get a newtsp */
			/* 1. Pick the first random element in the current tsp */
			int element2 = (int)((double)(newTspList.size()-1) * generator.nextDouble());
			int element1 = element2 - 1;
			if ( element1 == -1 ){
				element1 = newTspList.size()-1;
			}
			
                        int delta;

			/*
			 * search in the neighborhood specified
                         * if not specified search all
			 */
                        if ( kNeighborHood != -1 ) {
			  /* We want to search in the specified k=n neighborhoods of element1 */
			  delta= (int)(2*kNeighborHood*generator.nextDouble()) - kNeighborHood;
                         } else {
			  delta= (int)((newTspList.size()-1)*generator.nextDouble()) - (int)(newTspList.size()/2);
			}

			if ( delta == 0 ) {
				delta = 2;
			} else if ( delta == 1 ) {
				delta = 2;
			} else if ( delta == -1) {
				delta = -2; }

			int element4 = element2 + delta;

			if ( element4 <  0 ) {
				element4 = newTspList.size()+element4;
			}else if ( element4 >=  newTspList.size() ) {
				element4 = element4-(newTspList.size()-1);
			}

			int element3 = element4 -1;
			if ( element3 == -1 ){
				element3 = newTspList.size()-1;
			}


			/*  
			 *  the new tsp will have element2->element4.......element1->element3....
			 */
			Integer vertex_3 = newTspList.get(element3);
			newTspList.set(element3,newTspList.get(element2));
			newTspList.set(element2,vertex_3);

			/*
			 * from element2+1 to element3-1 swap to reverse their order
			 */
			while ( element2 < element3 ){
				element3 = element3-1;
				if ( element3 == -1 ) {
					element3 = newTspList.size()-1;
				}

				element2 = element2 + 1;
				if ( element2 == newTspList.size() ) {
					element3 = 0;
				}

				Integer tempVertex = newTspList.get(element2);
				newTspList.set(element2,newTspList.get(element3));
				newTspList.set(element3,tempVertex);

			}

			double newTspCost = tspCost(newTspList);

			/* if new local search solution is better than eariler take the search and continue search
			 */
			if ( newTspCost <= currentTspCost ){
				currentTspList = newTspList;
				currentTspCost = newTspCost;
			} else {
			/* if new local search solution is not better than eariler 
			 */
				continue;
			}
	
			//Subtract the start time from the finish time to get the actual algorithm running time; divide by 10e6 to convert to milliseconds
			double cycleTime = (System.nanoTime()-cycleStartTime);

			/* first improvement , take the best each time */
			if ( newTspCost < bestCost ) {
				bestCost = newTspCost;
				bestTspList = newTspList;
				bestCycleTime = cycleTime;
				/* print the trace file */
				traceString = String.format("%.2f, %d",Math.round(((System.nanoTime()-baseTime)/nanosecs)*100.0)/100.0,Math.round(bestCost));
				appendToTraceFile(runId,traceString);

				if ( bestCost <= optimalCost )
					break;
			}

		}

		/* print the sol file */
		printSol(runId,bestTspList,bestCost);

		/* print the tab results file */
		appendTabResults(runId,bestCycleTime,bestCost,bestTspList);

		return bestTspList;

	}


	/*
	 * Get the cost of the tsp
	 */
	private static double tspCost(LinkedList<Integer> tspList) {

	   	double tspCost=0;

		for (int i=1;i<tspList.size();i++){ 
		   tspCost += Project.sourceGTree.get(tspList.get(i-1)).get(tspList.get(i));
		}

		if ( tspList.getFirst().intValue() != tspList.getLast().intValue() ){
		   tspCost += Project.sourceGTree.get(tspList.getLast()).get(tspList.getFirst());
		}

		return tspCost;
	}
	


	/* 
	 * print the sol file 
	 * */
	private static void printSol(Integer runId,LinkedList<Integer> bestTspList, double bestCost) throws java.io.FileNotFoundException,UnsupportedEncodingException{

		// writing the sol
		// <inputFilename>_<method>_<cutoff>_<runID>*.sol
		String fileName = String.format("%s_%s_%.0f",Project.name,Project.method,Project.cutoffTimeSeconds);

		if ( runId != -1 ){
			fileName = fileName +"_"+runId+".sol";
		} else {
			fileName = fileName +".sol";
		}

		PrintWriter repWriter = new PrintWriter(fileName, "UTF-8");

		System.out.print("RESULTS :"); 
		System.out.print("file="+fileNameWithAbsolutePath.getName()); 
		System.out.print(":method="+method); 
		System.out.print(":randomSeed="+randomSeed);
		System.out.print(":cutOffTimeSeconds="+cutoffTimeSeconds);
		System.out.print(":timedOut="+timedOut);
		System.out.print(":BestCost="+bestCost);
		System.out.println(":OptimalCost="+Project.optimalCost);

		repWriter.printf("%d\n",Math.round(bestCost));

		for (int i=0;i<bestTspList.size();i++){
		 repWriter.printf("%d,",bestTspList.get(i));
		}
		if ( bestTspList.getFirst().intValue() != bestTspList.getLast().intValue() ){
		 repWriter.printf("%d,",bestTspList.getFirst());
		}

		repWriter.printf("\n");

	        repWriter.close();

	}

	/* 
	 * print the trace file 
	 *
	 */
	private static void printTrace(Integer runId, String traceString ) throws java.io.FileNotFoundException,UnsupportedEncodingException{

		// writing the trace report
		// <inputFilename>_<method>_<cutoff>_<runID>*.sol
		String fileName = String.format("%s_%s_%.0f",Project.name,Project.method,Project.cutoffTimeSeconds);

		if ( runId != -1 ){
			fileName = fileName +"_"+runId+".trace";
		} else {
			fileName = fileName +".trace";
		}

		PrintWriter repWriter = new PrintWriter(fileName, "UTF-8");

		repWriter.printf("%s\n",traceString);

	        repWriter.close();

	}

	/* 
	 * append to the trace file 
	 *
	 */
	private static void appendToTraceFile(Integer runId, String traceString ) throws java.io.FileNotFoundException,UnsupportedEncodingException{

		// writing the trace report
		// <inputFilename>_<method>_<cutoff>_<runID>*.sol
		String fileName = String.format("%s_%s_%.0f",Project.name,Project.method,Project.cutoffTimeSeconds);
			
		if ( runId != -1 ){
			fileName = fileName +"_"+runId+".trace";
		} else {
			fileName = fileName +".trace";
		}

		PrintWriter repWriter = new PrintWriter(new FileOutputStream(fileName,true));

		repWriter.printf("%s\n",traceString);

	        repWriter.close();

	}

	/* 
	 * print the tab results file 
	 * */
	private static void appendTabResults(Integer runId, double cpuTime,double bestCost,LinkedList<Integer> bestTspList) throws java.io.IOException,java.io.FileNotFoundException,UnsupportedEncodingException{

		// <inputFilename>_<method>_<cutoff>_<runID>*.sol
		String fileName = Project.method+".tab";
		FileOutputStream fo = new FileOutputStream(fileName,true);

		String bestTspPath = new String();
		for (int i=0;i<bestTspList.size();i++){
			bestTspPath += String.format("%d,",bestTspList.get(i));
		}

		if ( bestTspList.getFirst().intValue() != bestTspList.getLast().intValue() ){
			bestTspPath += String.format("%d",bestTspList.getFirst());
		}

		String msg = String.format("%s,%d,%.2f,%.0f,%d,%.2f,%d,%.0f,%s%n",
			fileNameWithAbsolutePath.getName(),runId,Math.round((cpuTime/nanosecs)*100.0)/100.0,optimalCost,Math.round(bestCost),
			Math.round(((bestCost-Project.optimalCost)/Project.optimalCost)*100.0)/100.0,randomSeed,cutoffTimeSeconds,bestTspPath);

		try {
    			java.nio.channels.FileLock lock = fo.getChannel().lock();
    			try {
				fo.write(msg.getBytes());
				fo.flush();
    			} finally {
        			lock.release();
    			}
		} finally {
    			fo.close();
		}


		/* 
	 	 * print the tab results by data file 
	         */
		// <inputFilename>_<method>_<cutoff>_<runID>*.sol
		fileName = Project.fileNameWithAbsolutePath.getName()+".tab";
		fo = new FileOutputStream(fileName,true);

		msg = String.format("%s,%d,%.2f,%.0f,%d,%.2f,%d,%.0f,%s%n",
			method,runId,Math.round((cpuTime/nanosecs)*100.0)/100.0,optimalCost,Math.round(bestCost),
			Math.round(((bestCost-Project.optimalCost)/Project.optimalCost)*100.0)/100.0,randomSeed,cutoffTimeSeconds,bestTspPath);

		try {
    			java.nio.channels.FileLock lock = fo.getChannel().lock();
    			try {
				fo.write(msg.getBytes());
				fo.flush();
    			} finally {
        			lock.release();
    			}
		} finally {
    			fo.close();
		}

	}


	/*
	 * Usage
	 */
	private static void printUsage(String msg){
		System.out.println("Failed :"+ msg);
		System.out.println("Usage :\n" +
		"java Project <file_name_with_full_absolute_path> <cutofftime_seconds> <method[mstapprox|heur_furthest_ins|ls_2opt_k5|ls_2opt_kall|ls_2opt_sa_k5|ls_2opt_sa_kall]> [<random_seed_for_random_search_algorithms>] [<runId-for local search algorithms>] " );
	}

	/*
	 * main  
	 */
	public static void main(String[] args) throws Exception{


		/*
		 * methods supported
		 */
		String[] methodsSupported = new String[] {"mstapprox","heur_furthest_ins","ls_2opt_k5","ls_2opt_kall","ls_2opt_sa_k5","ls_2opt_sa_kall"};

		/* 
		 * filename(with full path- otherwise looks up local directory)
		 * curoff time in secs
		 * method (mstapprox|heur_furthest_ins|ls_2opt_k5|ls_2opt_kall|ls_2opt_sa_k5|ls_2opt_sa_kall)
		 * seed - for random search algos only
		 */
		if(args.length >= 3) {
			Project.fileNameWithAbsolutePath = new File(args[0]);
	           	Project.cutoffTimeSeconds = new Double(Double.parseDouble(args[1]));
	           	Project.method = args[2];
                } else {
			printUsage("Insufficient Arguments Passed");
			return;
		}

		/* 
		 * random seed is optional for random search algorithms 
		 *   It is also used to get the first node in prim and furtherst node searches 
		 *    from the approx and greedy algos as well
		 */
		if(args.length >= 4) {
			Project.randomSeed = Long.parseLong(args[3]);
                }

		if (Project.randomSeed == 0 )
			Project.randomSeed = 5;
		
		Project.generator = new Random(Project.randomSeed);

		/* get a run Id is passed , valid for stochastic search algorithms */
		if ( Project.method.equals("ls_2opt_k5") || 
			Project.method.equals("ls_2opt_kall")|| 
			Project.method.equals("ls_2opt_sa_k5")|| 
			Project.method.equals("ls_2opt_sa_kall")) {
			if(args.length >= 5) {
				Project.runId = Integer.parseInt(args[4]);
                	}
		}

		/*
		 * Validations - method is supported
		 */
		boolean methodFound=false;
		for (String methodSupported : methodsSupported)
		{
		  if ( Project.method.equals(methodSupported)){
			  methodFound=true;
		  }
		}

		if ( !methodFound ){
			printUsage("Unsupported Method passed "+Project.method);
			return;
		}

		/*
		 * run the method used for tsp
		 */
		if (Project.method.equals("mstapprox")){
			mstApprox(fileNameWithAbsolutePath);
		} else if (Project.method.equals("heur_furthest_ins")){
			heurFurthestInsertion(fileNameWithAbsolutePath);
		} else if (Project.method.equals("ls_2opt_sa_k5")){
			localSearchSimulatedAnnealing(fileNameWithAbsolutePath,5);
		} else if (Project.method.equals("ls_2opt_sa_kall")){
			localSearchSimulatedAnnealing(fileNameWithAbsolutePath,-1);
		} else if (Project.method.equals("ls_2opt_k5")){
			localSearch2Opt(fileNameWithAbsolutePath,5);
		} else if (Project.method.equals("ls_2opt_kall")){
			localSearch2Opt(fileNameWithAbsolutePath,-1);
		}

	}
}
