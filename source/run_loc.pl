use strict;
use warnings;
use threads;

my $cTimeOutSecs=20*60;
my $cRandomRange=10000000;

my $path="java -cp  /home/ajdsouza/cse6140/project/zip/code/Project.jar Project /home/ajdsouza/cse6140/project/DATA/";

$path = "java -cp C:\\wk\\ajaysgoogledrive\\education\\gatech\\course\\cse6140_algorithms\\project\\zip\\code\\Project.jar Project C:\\wk\\ajaysgoogledrive\\education\\gatech\\course\\cse6140_algorithms\\project\\DATA\\" if $^O =~ /win/i;

sub run_tsp_algo($;){

 my($type) = @_;

 my $maxRunId = 1;

 print "Starting thread for $type\n";

 $maxRunId = 30 if ( $type =~ /ls_2opt_sa_k5|ls_2opt_sa_kall|ls_2opt_kall|ls_2opt_k5/ );

 foreach my $file ( qw ( ch150 gr202 )) {

   for my $runId (1..$maxRunId) {

  	my $random_seed = 1 + int(rand($cRandomRange));

   	my $cmd = "$path$file\.tsp $cTimeOutSecs $type $random_seed $runId";

   	print "$cmd\n";
	system($cmd) if @ARGV and $ARGV[0];

   }
 }

}


foreach my $type ( qw (ls_2opt_sa_k5 ) ) {
  my $thr = threads->create('run_tsp_algo',$type);
}

foreach my $thr (threads->list()) {
 $thr->join();
}
