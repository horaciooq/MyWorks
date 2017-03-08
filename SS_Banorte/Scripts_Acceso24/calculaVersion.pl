use strict;
use warnings;
use Cwd;

if(@ARGV > 0){
  #print scalar @ARGV;
  #print "\n";
}
my $dir = getcwd;

#print $dir . "\n";

opendir(my $dh, $dir) || die "Can't opendir $dir: $!";
my @versiones = grep { /^\d+$/ && -d "$dir/$_" } readdir($dh);
closedir $dh;
#print @versiones;
if(@versiones > 0){
  @versiones = sort {$b <=> $a} @versiones;
  print $versiones[0];
  
}else{
  print 0;
  #print "\n";
}
#print $versiones[0]; for /f %%i in ('application arg0 arg1') do set VAR=%%i
