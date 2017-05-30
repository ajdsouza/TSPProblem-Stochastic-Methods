import java.util.HashMap; 
import java.util.LinkedList; 
import java.util.Map; 
import java.util.Random; 

public final class FurthestInsertion{

    /* 
     * class a container for Edge tuple 
     */
    private static class Edge {
    	Integer v1;
    	Integer v2;
    }

    /**
     * For the node passed add the edges from the current node to every other node
     * as long as that node has already not been explored
     *
     */
    private static void addEdgesFromCurrentNodeToHeap(int node, HashMap<Integer,HashMap<Integer,Double>> sourceGTree,
                                             LinkedList<Integer> tspList,
                                             FibonacciHeap<Integer> pq,
                                             Map<Integer, FibonacciHeap.Entry<Integer>> entries ) {

	/* all edges from node */
        for (Map.Entry<Integer, Double> edge : sourceGTree.get(node).entrySet()) {
            /* cycle */
            if (tspList.contains(edge.getKey())) continue; 

	    /* node is not in the fib heap, enqueue it */
            if (!entries.containsKey(edge.getKey())) { 
                entries.put(edge.getKey(), pq.enqueue(edge.getKey(), edge.getValue()*-1));
            }
	    else if (entries.get(edge.getKey()).getPriority() > edge.getValue()*-1) { 
            /* this is a better edge, so update the fib heap */
                pq.decreaseKey(entries.get(edge.getKey()), edge.getValue()*-1);
            }

        }
    }


    /**
     * from the max value Node passed, find a pair fo vertices in the tsp already where
     * this max value node can be inserted between
     */
    private static Edge getEdgesToBeReplacedWithPathToMaxValNode(int maxValueNode,  HashMap<Integer,HashMap<Integer,Double>> sourceGTree, 
                                          LinkedList<Integer> tspList) {
        Edge edge = null;
        double minWeight = Double.POSITIVE_INFINITY;

	/* looping over unexplored nodes from nextMinValNode  to get the miminal c_ki+c_kj-cij */
	for (int i = 0; i < tspList.size(); i++) {

	            int v1 = tspList.get(i);
		    int v2;
		    if ( i+1 >= tspList.size() )
	             v2 = tspList.get(0);
		    else
	             v2 = tspList.get(i+1);

		    /* keep track of the edge where insertion gives minimal cost increase */
		    if ( ( sourceGTree.get(maxValueNode).get(v1) + sourceGTree.get(maxValueNode).get(v2) - sourceGTree.get(v1).get(v2) ) < minWeight ){
			    minWeight = sourceGTree.get(maxValueNode).get(v1) + sourceGTree.get(maxValueNode).get(v2) - sourceGTree.get(v1).get(v2);
			    edge = new Edge();
			    edge.v1 = v1;
			    edge.v2 = v2;
		    }
        }

        return edge;
    }


    /**
     * generate the tsp based on Farthest Insertion
     */
    public static LinkedList<Integer> generateTSP(HashMap<Integer,HashMap<Integer,Double>> sourceGTree, long randomSeed) {
        /* this is the fibonacci heap taken from standard implementation from Keith Schwarz htiek@cs.stanford.edu */
        FibonacciHeap<Integer> pq = new FibonacciHeap<Integer>();

        Map<Integer, FibonacciHeap.Entry<Integer>> entries = new HashMap<Integer, FibonacciHeap.Entry<Integer>>();

        /* the mst to be built */
        LinkedList<Integer> tspList = new LinkedList<Integer>();

        /* we start with a randon node */
	Object[] nodes = sourceGTree.keySet().toArray();
        Integer startNode = (Integer)nodes[new Random(randomSeed).nextInt(nodes.length-1)];

	//System.out.println("startNode = "+startNode);
	tspList.addFirst(startNode);

	/*
	 * populate the fib heap with the nodes that have a edge to this node
	 */
        addEdgesFromCurrentNodeToHeap(startNode, sourceGTree, tspList, pq, entries);

	/*
	 * from the fibonaci heap get the node with max value (min -ve value)
	 */
         int maxValNode = pq.dequeueMin().getValue();

	/*
	 * insert the maxValNode ts second vertices in the edge
	 */
	tspList.add(maxValNode);

	/*
	 * populate the fib heap with the nodes that have a edge to this node
	 */
        addEdgesFromCurrentNodeToHeap(maxValNode, sourceGTree, tspList, pq, entries);

	/*
	 * pick the best node from the current set of explored nodes , with lowest weight
	 */
        for (int i = 0; i < sourceGTree.size()-2 ; ++i) {

		/*
		 * from the fibonaci heap get the node with max value (min -ve value)
		 */
            	maxValNode = pq.dequeueMin().getValue();

                /* 
		 * Get the edge to be removed in tsp and 
		 * between which this node can be inserted based on the minimal cost increase
                 */
                 Edge edge = getEdgesToBeReplacedWithPathToMaxValNode(maxValNode, sourceGTree, tspList);

		 /*
		  * insert the maxValNode between the vertices in the edge
		  */
		 tspList.add(tspList.indexOf(edge.v2),maxValNode);
    
                /* Explore outward from this node. */
                addEdgesFromCurrentNodeToHeap(maxValNode, sourceGTree, tspList, pq, entries);
        }

	/* add the path back from last node to first node */
	tspList.addLast(tspList.getFirst());

        /* Hand back the generated tsp. */
        return tspList;
    }


};

