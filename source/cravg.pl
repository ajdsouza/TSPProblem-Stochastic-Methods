use strict;
use warnings;
use Data::Dumper;

my $dir=".";
$dir = $ARGV[0] if @ARGV;

opendir(DIR, $dir) or die "cannot open directory $dir";
my @allfiles = readdir(DIR);
my %res;

foreach my $file ( qw (burma14 ulysses16 berlin52 kroA100 ch150 gr202 )) {

  my $filepattern = "$file\.tsp\.tab";
  my @docs =  grep {/^$filepattern/} @allfiles;

  foreach my $filename (@docs) {

        open (RES, $filename ) or die "could not open $filename\n";

	while  (<RES>) {

		my $ln = $_;
		$ln =~ s/\s+// if $ln;
		next unless $ln;

		my ($type,$runId,$cpuTime,$optimal,$cost,$quality,$randomSeed,$cutofftime,$path) =
			($ln =~ /([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),(.+)$/);


	        if ( $res{best_cost} and $res{best_cost}{$file} and $res{best_cost}{$file}{$type} ){
			if ( $res{best_cost}{$file}{$type}{cost} > $cost ) {
				$res{best_cost}{$file}{$type}{cost} = $cost;
				$res{best_cost}{$file}{$type}{runId} = $runId;
				$res{best_cost}{$file}{$type}{randomSeed} = $randomSeed;
				$res{best_cost}{$file}{$type}{path} = $path;
				$res{best_cost}{$file}{$type}{cpuTime} = $cpuTime;
				$res{best_cost}{$file}{$type}{cutofftime} = $cutofftime;
				$res{best_cost}{$file}{$type}{quality} = $quality;
				$res{best_cost}{$file}{$type}{optimal} = $optimal;
			}
		} else {
				$res{best_cost}{$file}{$type}{cost} = $cost;
				$res{best_cost}{$file}{$type}{runId} = $runId;
				$res{best_cost}{$file}{$type}{randomSeed} = $randomSeed;
				$res{best_cost}{$file}{$type}{path} = $path;
				$res{best_cost}{$file}{$type}{cpuTime} = $cpuTime;
				$res{best_cost}{$file}{$type}{cutofftime} = $cutofftime;
				$res{best_cost}{$file}{$type}{quality} = $quality;
				$res{best_cost}{$file}{$type}{optimal} = $optimal;
				$res{best_cost}{$file}{$type}{sum_cost} = 0;
				$res{best_cost}{$file}{$type}{sum_cpuTime} = 0;
				$res{best_cost}{$file}{$type}{num} = 0;
		}
	
		$res{best_cost}{$file}{$type}{sum_cost} += $cost;
		$res{best_cost}{$file}{$type}{sum_cpuTime} += $cpuTime;
		$res{best_cost}{$file}{$type}{sum_quality} += $quality;
		$res{best_cost}{$file}{$type}{num} += 1;
	
	
	        if ( $res{bestest_cost} and $res{bestest_cost}{$file} ){
	                if ( $res{bestest_cost}{$file}{cost} > $cost ) {
	                        $res{bestest_cost}{$file}{cost} = $cost;
	                        $res{bestest_cost}{$file}{optimal} = $optimal;
	                        $res{bestest_cost}{$file}{type} = $type;
	                        $res{bestest_cost}{$file}{runId} = $runId;
	                        $res{bestest_cost}{$file}{randomSeed} = $randomSeed;
	                        $res{bestest_cost}{$file}{path} = $path;
	                        $res{bestest_cost}{$file}{cpuTime} = $cpuTime;
	                        $res{bestest_cost}{$file}{quality} = $quality;
	                        $res{bestest_cost}{$file}{cutofftime} = $cutofftime;
	                }
	        } else {
	                        $res{bestest_cost}{$file}{cost} = $cost;
	                        $res{bestest_cost}{$file}{optimal} = $optimal;
	                        $res{bestest_cost}{$file}{type} = $type;
	                        $res{bestest_cost}{$file}{runId} = $runId;
	                        $res{bestest_cost}{$file}{randomSeed} = $randomSeed;
	                        $res{bestest_cost}{$file}{path} = $path;
	                        $res{bestest_cost}{$file}{cpuTime} = $cpuTime;
	                        $res{bestest_cost}{$file}{quality} = $quality;
	                        $res{bestest_cost}{$file}{cutofftime} = $cutofftime;
	        }

	}

  }

  for my $file ( keys %{$res{best_cost}} ){
  	for my $type ( keys %{$res{best_cost}{$file}} ){
		$res{best_cost}{$file}{$type}{avg_cost} = $res{best_cost}{$file}{$type}{sum_cost} / $res{best_cost}{$file}{$type}{num};
		$res{best_cost}{$file}{$type}{avg_cpuTime} =  $res{best_cost}{$file}{$type}{sum_cpuTime} / $res{best_cost}{$file}{$type}{num};
		$res{best_cost}{$file}{$type}{avg_quality} =  $res{best_cost}{$file}{$type}{sum_quality} / $res{best_cost}{$file}{$type}{num};
  	}
  }

}

#print tab results
print "TAB RESULTS - BY FILE\n";
for my $file ( keys %{$res{best_cost}} ){
  	for my $type ( keys %{$res{best_cost}{$file}} ){
		printf "%40s","$file - $type =";
		print "  $res{best_cost}{$file}{$type}{avg_cost} ";
		print "- $res{best_cost}{$file}{$type}{avg_cpuTime} "; 
		print "- $res{best_cost}{$file}{$type}{avg_quality} "; 
		print "- $res{best_cost}{$file}{$type}{num} "; 
		print "- $res{best_cost}{$file}{$type}{cost} "; 
		print "- $res{best_cost}{$file}{$type}{optimal} "; 
		print "- $res{best_cost}{$file}{$type}{quality} "; 
		print "- $res{best_cost}{$file}{$type}{runId} "; 
		print "- $res{best_cost}{$file}{$type}{randomSeed} "; 
		print "- $res{best_cost}{$file}{$type}{cutofftime} "; 
		print "- $res{best_cost}{$file}{$type}{cpuTime} "; 
		#print "- $res{best_cost}{$file}{$type}{path} "; 
		print "\n";
  	}
}


#print best results
print "BESTEST RESULTS - BY FILE\n";
for my $file ( keys %{$res{bestest_cost}} ){
	print "$file ";
	print "- $res{bestest_cost}{$file}{type} "; 
	print "- $res{bestest_cost}{$file}{cost} "; 
	print "- $res{bestest_cost}{$file}{optimal} "; 
	print "- $res{bestest_cost}{$file}{quality} "; 
	print "- $res{bestest_cost}{$file}{runId} "; 
	print "- $res{bestest_cost}{$file}{randomSeed} "; 
	print "- $res{bestest_cost}{$file}{cutofftime} "; 
	print "- $res{bestest_cost}{$file}{cpuTime} "; 
	print "- $res{bestest_cost}{$file}{path} "; 
	print "\n";
}

