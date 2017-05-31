TSP Problem - Comparison of the Exact, Heuristic and Stochastic Local Search Solutions
---------------------------------------------------------------------------------------
The data is provided as a set of nodes(cities) where costs between nodes are computed as Euclidean distances or Geographical distances as the case may be. The goal is to explore and compare the different methods of finding the lowest cost Hamiltonian Cycle for the graph and solve the TSP Problem for that graph.

The TSP problem is NP-Hard. We can verify an solution is an Hamiltonian Cycle in Poly Time but a poly time optimal solution algorithm is not known. For an exact solition the number of solutions to be explored is N!. For Large N this is a vast solution space and intractable for an exact solution, so we need to look at Heuristic and Stochastic Methods at solving the TSP problem

Here we implement solutions for the following three methods and compare and contrast their results

1.  Exact Solution - Using the Branch and Bound with the lower bound as the MST Approx solution. Break Cycle for large N

			MST-Approx:
			Prim(for dense edges) or Kruskal give an MST in approx O(ElogV) time. 
			Traverse the MST depth first for a full walk (an eulerian traverse thru the MST) and keep an preorder sequence of veertices ( sequnce of vertices traversed)
 			The TSP cannot be less than MST. And the preOrder sequence cannot be more than twice lowest cost TSP.
			Christofedes Algorithm gives a tigher 1.5 approximation
				
2.  Heuristic Solution with and without Approximation Guarantees - Using MST-Approx and Farthest Insert Methods
			Farthest Insertion (Prims with negative cost and  choosing jk):
				At each step find the edge with the largest cost c_ir to the subtour r foind so far
				pick j,k in the subtour to insert i inbetween j and k whcih maximizes c_ji+c_ik-cjk
				O(n^2)

3.  Stochastic Solution - Local Search Using 2 Opt Exchange with a combination of Simulated Annealing, Iterative Search, Random Walk with different K for search neighborhoods
			A 2opt swap:
				i,j  -> connect i-1 to j and i to j+1
				(Path from i to j is reversed and connected to i-1)
 				The choice of i,j is random in a K-neighborhood
				Use Local Search with Simulated annealing to get find global optimum.
					Iniitally as Temp is large we accept TSP which have a higher cost then current TSP in the hops of finding a global optimum
					As tempreature cools we tend to accept more local optimums


Implementation
--------------
1. The project is implemented in Project.class. This is the top level program. 

2. All the class files are compiled on windows 8, 64 bit with Intel Core I7 2.5 Ghz 
processor and 16GB of memory box and are archived in Project.jar

3. To execute the project
java -cp <classpath if extracted the class files from jar or the path 
      to the jar file>Project.jar Project
<file_name_with_full_absolute_path> <cutofftime_seconds> <method[mstapprox|heur_furthest_ins|ls_2opt_k5|ls_2opt_kall|ls_2opt_sa_k5|ls_2opt_sa_kall]> [<random_seed_for_random_search_algorithms>] [<runId-for local search algorithms>]

echo "MST Approx"
java -cp Project.jar Project ..\\data\\burma14.tsp 100 mstapprox
java -cp Project.jar Project ..\\data\\ulysses16.tsp 100 mstapprox
java -cp Project.jar Project ..\\data\\berlin52.tsp 100 mstapprox
java -cp Project.jar Project ..\\data\\kroA100.tsp 100 mstapprox
java -cp Project.jar Project ..\\data\\ch150.tsp 100 mstapprox
java -cp Project.jar Project ..\\data\\gr202.tsp 100 mstapprox

echo "Heuristic Furthest Insertion"
java -cp Project.jar Project ..\\data\\burma14.tsp 100 heur_furthest_ins
java -cp Project.jar Project ..\\data\\ulysses16.tsp 100 heur_furthest_ins
java -cp Project.jar Project ..\\data\\berlin52.tsp 100 heur_furthest_ins
java -cp Project.jar Project ..\\data\\kroA100.tsp 100 heur_furthest_ins
java -cp Project.jar Project ..\\data\\ch150.tsp 100 heur_furthest_ins
java -cp Project.jar Project ..\\data\\gr202.tsp 100 heur_furthest_ins

echo "Local Search - 2opt  with k5 neighborhood "
java -cp Project.jar Project ..\\data\\burma14.tsp 100 ls_2opt_k5 5 1
java -cp Project.jar Project ..\\data\\ulysses16.tsp 100 ls_2opt_k5 5 1
java -cp Project.jar Project ..\\data\\berlin52.tsp 100 ls_2opt_k5 5 1
java -cp Project.jar Project ..\\data\\kroA100.tsp 100 ls_2opt_k5 5 1
java -cp Project.jar Project ..\\data\\ch150.tsp 100 ls_2opt_k5 5 1
java -cp Project.jar Project ..\\data\\gr202.tsp 100 ls_2opt_k5 5 1

echo "Local Search - 2opt with Simulated Annealing with k5 neighborhood "
java -cp Project.jar Project ..\\data\\burma14.tsp 100 ls_2opt_sa_k5 5 1
java -cp Project.jar Project ..\\data\\ulysses16.tsp 100 ls_2opt_sa_k5 5 1
java -cp Project.jar Project ..\\data\\berlin52.tsp 100 ls_2opt_sa_k5 5 1
java -cp Project.jar Project ..\\data\\kroA100.tsp 100 ls_2opt_sa_k5 5 1
java -cp Project.jar Project ..\\data\\ch150.tsp 100 ls_2opt_sa_k5 5 1
java -cp Project.jar Project ..\\data\\gr202.tsp 100 ls_2opt_sa_k5 5 1

echo "Local Search - 2opt  with full neighborhood "
java -cp Project.jar Project ..\\data\\burma14.tsp 100 ls_2opt_kall 5 1
java -cp Project.jar Project ..\\data\\ulysses16.tsp 100 ls_2opt_kall 5 1
java -cp Project.jar Project ..\\data\\berlin52.tsp 100 ls_2opt_kall 5 1
java -cp Project.jar Project ..\\data\\kroA100.tsp 100 ls_2opt_kall 5 1
java -cp Project.jar Project ..\\data\\ch150.tsp 100 ls_2opt_kall 5 1
java -cp Project.jar Project ..\\data\\gr202.tsp 100 ls_2opt_kall 5 1

echo "Local Search - 2opt with Simulated Annealing with full neighborhood "
java -cp Project.jar Project ..\\data\\burma14.tsp 100 ls_2opt_sa_kall 5 1
java -cp Project.jar Project ..\\data\\ulysses16.tsp 100 ls_2opt_sa_kall 5 1
java -cp Project.jar Project ..\\data\\berlin52.tsp 100 ls_2opt_sa_kall 5 1
java -cp Project.jar Project ..\\data\\kroA100.tsp 100 ls_2opt_sa_kall 5 1
java -cp Project.jar Project ..\\data\\ch150.tsp 100 ls_2opt_sa_kall 5 1
java -cp Project.jar Project ..\\data\\gr202.tsp 100 ls_2opt_sa_kall 5 1


3.1  The perl script run.pl will execute all the algorithms for all the files.
For local search algorithms it will generate 30 runs for each file for an
algorithm with different runIds and different random seeds. Each algorithm is executed in
a different threads, so the execution runs in parallel .

4.The output files are generated in the SAME directory from where you are
executing the program.

5.The files geneated by execution of the program for the given input in homework 
are

berlin52_heur_furthest_ins_100_1.sol  
burma14_heur_furthest_ins_100_1.sol  
ch150_heur_furthest_ins_100_1.sol  
gr202_heur_furthest_ins_100_1.sol  
kroA100_heur_furthest_ins_100_1.sol  
ulysses16.tsp_heur_furthest_ins_100_1.sol
berlin52_ls_2opt_sa_100_1.sol         
burma14_ls_2opt_sa_100_1.sol         
ch150_ls_2opt_sa_100_1.sol         
gr202_ls_2opt_sa_100_1.sol         
kroA100_ls_2opt_sa_100_1.sol         
ulysses16.tsp_ls_2opt_sa_100_1.sol
berlin52_mstapprox_100_1.sol          
burma14_mstapprox_100_1.sol          
ch150_mstapprox_100_1.sol          
gr202_mstapprox_100_1.sol          
kroA100_mstapprox_100_1.sol          
ulysses16.tsp_mstapprox_100_1.sol

berlin52_heur_furthest_ins_100_1.trace  
burma14_heur_furthest_ins_100_1.trace  
ch150_heur_furthest_ins_100_1.trace  
gr202_heur_furthest_ins_100_1.trace  
kroA100_heur_furthest_ins_100_1.trace  
ulysses16.tsp_heur_furthest_ins_100_1.trace
berlin52_ls_2opt_sa_100_1.trace         
burma14_ls_2opt_sa_100_1.trace         
ch150_ls_2opt_sa_100_1.trace         
gr202_ls_2opt_sa_100_1.trace         
kroA100_ls_2opt_sa_100_1.trace         
ulysses16.tsp_ls_2opt_sa_100_1.trace
berlin52_mstapprox_100_1.trace          
burma14_mstapprox_100_1.trace          
ch150_mstapprox_100_1.trace          
gr202_mstapprox_100_1.trace          
kroA100_mstapprox_100_1.trace          
ulysses16.tsp_mstapprox_100_1.trace

7. The files processed are printed to stdout
