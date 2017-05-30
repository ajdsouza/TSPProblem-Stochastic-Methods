1. The project is implemented in Project.class. This is the top level program.

2. All the class files are compiled on windows 8, 64 bit with Intel Core I7 2.5 Ghz 
processor and 16GB of memory box and are archived in Project.jar

3. To execute the project
java -cp <classpath if extracted the class files from jar or the path 
      to the jar file>Project.jar Project
<file_name_with_full_absolute_path> <cutofftime_seconds> <method[mstapprox|heur_furthest_ins|ls_2opt_k5|ls_2opt_kall|ls_2opt_sa_k5|ls_2opt_sa_kall]> [<random_seed_for_random_search_algorithms>] [<runId-for local search algorithms>]

echo "MST Approx"
java -cp Project.jar Project C:\\wk\\project\\DATA\\burma14.tsp 100 mstapprox
java -cp Project.jar Project C:\\wk\\project\\DATA\\ulysses16.tsp 100 mstapprox
java -cp Project.jar Project C:\\wk\\project\\DATA\\berlin52.tsp 100 mstapprox
java -cp Project.jar Project C:\\wk\\project\\DATA\\kroA100.tsp 100 mstapprox
java -cp Project.jar Project C:\\wk\\project\\DATA\\ch150.tsp 100 mstapprox
java -cp Project.jar Project C:\\wk\\project\\DATA\\gr202.tsp 100 mstapprox

echo "Heuristic Furthest Insertion"
java -cp Project.jar Project C:\\wk\\project\\DATA\\burma14.tsp 100 heur_furthest_ins
java -cp Project.jar Project C:\\wk\\project\\DATA\\ulysses16.tsp 100 heur_furthest_ins
java -cp Project.jar Project C:\\wk\\project\\DATA\\berlin52.tsp 100 heur_furthest_ins
java -cp Project.jar Project C:\\wk\\project\\DATA\\kroA100.tsp 100 heur_furthest_ins
java -cp Project.jar Project C:\\wk\\project\\DATA\\ch150.tsp 100 heur_furthest_ins
java -cp Project.jar Project C:\\wk\\project\\DATA\\gr202.tsp 100 heur_furthest_ins

echo "Local Search - 2opt  with k5 neighborhood "
java -cp Project.jar Project C:\\wk\\project\\DATA\\burma14.tsp 100 ls_2opt_k5 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\ulysses16.tsp 100 ls_2opt_k5 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\berlin52.tsp 100 ls_2opt_k5 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\kroA100.tsp 100 ls_2opt_k5 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\ch150.tsp 100 ls_2opt_k5 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\gr202.tsp 100 ls_2opt_k5 5 1

echo "Local Search - 2opt with Simulated Annealing with k5 neighborhood "
java -cp Project.jar Project C:\\wk\\project\\DATA\\burma14.tsp 100 ls_2opt_sa_k5 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\ulysses16.tsp 100 ls_2opt_sa_k5 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\berlin52.tsp 100 ls_2opt_sa_k5 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\kroA100.tsp 100 ls_2opt_sa_k5 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\ch150.tsp 100 ls_2opt_sa_k5 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\gr202.tsp 100 ls_2opt_sa_k5 5 1

echo "Local Search - 2opt  with full neighborhood "
java -cp Project.jar Project C:\\wk\\project\\DATA\\burma14.tsp 100 ls_2opt_kall 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\ulysses16.tsp 100 ls_2opt_kall 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\berlin52.tsp 100 ls_2opt_kall 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\kroA100.tsp 100 ls_2opt_kall 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\ch150.tsp 100 ls_2opt_kall 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\gr202.tsp 100 ls_2opt_kall 5 1

echo "Local Search - 2opt with Simulated Annealing with full neighborhood "
java -cp Project.jar Project C:\\wk\\project\\DATA\\burma14.tsp 100 ls_2opt_sa_kall 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\ulysses16.tsp 100 ls_2opt_sa_kall 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\berlin52.tsp 100 ls_2opt_sa_kall 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\kroA100.tsp 100 ls_2opt_sa_kall 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\ch150.tsp 100 ls_2opt_sa_kall 5 1
java -cp Project.jar Project C:\\wk\\project\\DATA\\gr202.tsp 100 ls_2opt_sa_kall 5 1


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
