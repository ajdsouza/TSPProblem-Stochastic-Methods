import java.util.HashMap; 
import java.util.LinkedList; 
import java.util.Map; 
import java.util.Random; 

public final class PrimsMST{


    /**
     * For the node passed add the edges from the current node to every other node
     * as long as that node has already not been explored
     *
     */
    private static void addEdgesFromCurrentNodeToHeap(int node, HashMap<Integer,HashMap<Integer,Double>> sourceGTree,
                                             LinkedList<Integer> nodesInMstree,
                                             FibonacciHeap<Integer> pq,
                                             Map<Integer, FibonacciHeap.Entry<Integer>> entries ) {

	/* all edges from node */
        for (Map.Entry<Integer, Double> edge : sourceGTree.get(node).entrySet()) {
            /* cycle */
            if (nodesInMstree.contains(edge.getKey())) continue; 

	    /* node is not in the fib heap, enqueue it */
            if (!entries.containsKey(edge.getKey())) { 
                entries.put(edge.getKey(), pq.enqueue(edge.getKey(), edge.getValue()));
            }
	    else if (entries.get(edge.getKey()).getPriority() > edge.getValue()) { 
            /* this is a better edge, so update the fib heap */
                pq.decreaseKey(entries.get(edge.getKey()), edge.getValue());
            }

        }
    }


    /**
     * from the min value Node, find the lowest cost edge to the 
     * already explored nodes
     */
    private static int getCheapestEdgeToExploredNodes(int nextMinValNode,  
			HashMap<Integer,HashMap<Integer,Double>> sourceGTree, 
			LinkedList<Integer> nodesInMstree) {
        int newNode=0;
        double cheapestWeight = Double.POSITIVE_INFINITY;

	/* looping over unexplored nodes from nextMinValNode */
        for (Map.Entry<Integer, Double> edge : sourceGTree.get(nextMinValNode).entrySet()) {

            /* not yet in mst */
            if (!nodesInMstree.contains(edge.getKey())) continue;

            /* this edge is not better */
            if ( edge.getValue() >= cheapestWeight ) continue;

	    /* this is better , so save it */
            newNode = edge.getKey();
            cheapestWeight = edge.getValue();
        }

        return newNode;
    }


    /**
     * generate the mst
     */
    public static HashMap<Integer,LinkedList<Integer>> generateMST(HashMap<Integer,HashMap<Integer,Double>> sourceGTree, long randomSeed) {
        /* this is the fibonacci heap taken from standard implementation from Keith Schwarz htiek@cs.stanford.edu */
        FibonacciHeap<Integer> pq = new FibonacciHeap<Integer>();

        Map<Integer, FibonacciHeap.Entry<Integer>> entries = new HashMap<Integer, FibonacciHeap.Entry<Integer>>();

        /* the mst to be built */
        LinkedList<Integer> nodesInMstree = new LinkedList<Integer>();

        /* the mst to be returned */
        HashMap<Integer,LinkedList<Integer>> mst = new HashMap<Integer,LinkedList<Integer>>();

        /* we start with a randon node */
	Object[] nodes = sourceGTree.keySet().toArray();
        Integer startNode = (Integer)nodes[new Random(randomSeed).nextInt(nodes.length-1)];

	//System.out.println("startNode = "+startNode);
	nodesInMstree.add(startNode);
 	mst.put(0,new LinkedList<Integer>());
	mst.get(0).add(startNode);

	/*
	 * populate the fib heap with the nodes that have a edge to this node
	 */
        addEdgesFromCurrentNodeToHeap(startNode, sourceGTree, nodesInMstree, pq, entries);

	/*
	 * pick the best node from the current set of explored nodes , with lowest weight
	 */
        for (int i = 0; i < sourceGTree.size()-1 ; ++i) {

		/*
		 * from the fibonaci heap get the node with min value
		 */
            	int minValNode = pq.dequeueMin().getValue();

                /* Determine which edge we should pick to add to the MST.  We'll
                 * do this by getting the endpoint of the edge leaving the current
                 * node that's of minimum cost and that enters the visited edges.
                 */
                 int cheapestEdgeNode = getCheapestEdgeToExploredNodes(minValNode, sourceGTree, nodesInMstree);
		 //System.out.println(cheapestEdgeNode+"->"+minValNode+"="+sourceGTree.get(minValNode).get(cheapestEdgeNode));
    
                 /* Add this edge to the list of nodes in the mstree. */
                 nodesInMstree.add(minValNode);

		 /*
		  * Maintain the edges in mst to be returned back
		  */
		 if (!mst.containsKey(cheapestEdgeNode)){
			 mst.put(cheapestEdgeNode,new LinkedList<Integer>());
		 }
		 mst.get(cheapestEdgeNode).add(minValNode);
    
                /* Explore outward from this node. */
                addEdgesFromCurrentNodeToHeap(minValNode, sourceGTree, nodesInMstree, pq, entries);
        }

        /* Hand back the generated sourceGTree. */
        return mst;
    }


};

