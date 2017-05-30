use strict;
use warnings;

my $dir=".";
$dir = $ARGV[0] if @ARGV;

opendir(DIR, $dir) or die "cannot open directory $dir";
my @allfiles = readdir(DIR);

my %optcost;
$optcost{burma14}=3323;
$optcost{ulysses16}=6859;
$optcost{berlin52}=7542;
$optcost{kroA100}=21282;
$optcost{ch150}=6528;
$optcost{gr202}=40160;

my %qrtd;
my %qrtd_count;
my %sqd;
my %sqd_count;

foreach my $type ( qw (ls_2opt_sa_k5 ls_2opt_k5 ) ) {

 foreach my $file ( qw (burma14 ulysses16 berlin52 kroA100 ch150 gr202 )) {

  my $filepattern = "$file\_$type";
  my @docs =  grep {/^$filepattern/} @allfiles;

  my $fileCount = 0;
  foreach my $filename (@docs) {

	next unless $filename =~ /trace$/;

        open (RES, $filename ) or die "could not open $filename\n";

	my $lastline;
        my $tm;
	my $cost;
	my $ql;
	while (<RES>) {
		my $line = $_;
		$line =~ s/\s+// if $line;
		next unless $line;
		$lastline = $line;

		my @args = split/,/,$lastline;
        	$tm = $args[0];
        	$cost = $args[1];
        	$ql = (100*($cost-$optcost{$file}))/$optcost{$file};

		push @{$sqd{$type}{$file}{$tm}},$ql;

		if ( $sqd_count{$type} and $sqd_count{$type}{$file} ) {
			$sqd_count{$type}{$file} += 1;
		} else {
			$sqd_count{$type}{$file} = 1;
		}

	}

	my $qr = sprintf("%.1f",$ql);		

	push @{$qrtd{$type}{$file}{$qr}}, $tm;		

	if ( $qrtd_count{$type} and $qrtd_count{$type}{$file} ) {
		$qrtd_count{$type}{$file} += 1;
	} else {
		$qrtd_count{$type}{$file} = 1;
	}
        
  }

 }

}

print "type,file,qr,tm,ratio\n";

for my $type ( keys %qrtd ) {
	for my $file ( keys %{$qrtd{$type}} ) {
		for my $qr ( sort {$a <=> $b } keys %{$qrtd{$type}{$file}} ) {
			my $cnt = 1;
			my @vals = sort {$a <=> $b } @{$qrtd{$type}{$file}{$qr}};
			my $total_count = @vals;
			for my $tm ( sort {$a <=> $b } @vals ) {
				print "$type,$file,$qr,";
				print "$tm,";
				my $ratio = $cnt / $total_count;
				printf "%.2f", $ratio;
				print "\n";
				$cnt += 1;
			}
		}
	}
}


print "type,file,tm,qr,ratio\n";

for my $type ( keys %sqd ) {
        for my $file ( keys %{$sqd{$type}} ) {
                for my $tm ( sort {$a <=> $b } keys %{$sqd{$type}{$file}} ) {
                	my $cnt = 1;
			my @vals = sort {$a <=> $b } @{$sqd{$type}{$file}{$tm}};
			my $total_count = @vals;
			for my $ql ( sort {$a <=> $b } @vals ) {
                                print "$type,$file,$tm,";
                                printf "%.1f" , $ql;
                                my $ratio = $cnt / $total_count;
                                printf ",%.2f", $ratio;
                                print "\n";
                                $cnt += 1;
                        }
                }
        }
}


