#Script para cambiar el WAS_HOME
$archivo = $ARGV[0];
open(fh, '<:encoding(UTF-8)', $archivo) or die 'no se puede abrir "' . $archivo . ' "';
@data=<fh>;
$strarchivo;
my $was_home = $ENV{'WAS_Portal_HOME'};
$was_home =~ s/\\/\//g;

foreach $line (@data){

	if($line=~/^\s*WAS_8_Portal/){
		#$aux=s/WAS_8_HOME/#WAS_8_HOME/;
		$strarchivo = $strarchivo."WAS_8_Portal=" . $was_home . "\n";
	}else{
		$strarchivo = $strarchivo . $line . "\n";
	}

}

open($fh2,'>',$ARGV[0]) or die "no se puede abrir";
print $fh2 $strarchivo; 
