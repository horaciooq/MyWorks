##Subida de archivos a Data power##
use MIME::Base64 qw(encode_base64);
use XML::XPath;
use Data::Dumper;

#Declaracion de variables para pruebas
#$curl='D:\curl\curl\curl\bin\curl.exe';#estatico
#$RTC="https://lnxsdtp1d.dev.unix.banorte.com:5550/service/mgmt/current";#variable
#$user="openmake";#variable
#$pass="Banorte123";#variable
#$dir='C:\OpenMake\DATAPOWER\Archivos\Desarrollo';#variable
#$domain="MCA_Desarrollo";#variable
#$local="local:///test///";
#my $xml = $ARGV[0];#variable
#declaracion de variables
$curl=$ARGV[0];
my $xml = $ARGV[1];#variable
$xml_tmp=$ARGV[2];
$dir=$ARGV[3];#variable
$domain=$ARGV[4];#variable
$local=$ARGV[5];
$user=$ARGV[6];#variable
$pass=$ARGV[7];#variable
$RTC=$ARGV[8];#variable


sub base64_encode{
	$str="";
	$archivo=@_[0];
	open(FILE, $archivo) or die "$!";
	while (read(FILE, $buf, 60*57)) {
		$str=$str.$buf;
	}
	$encode = encode_base64($str);
	return $encode;
}

sub Crear_archivo_teporal {
	$archivo=@_[0];
	$tipo=@_[1];
	$name=$local.$tipo."///".&get_filename($archivo);
	$encoded=&base64_encode($archivo);
    my $nodeset  = XML::XPath->new(filename => $xml);  
	foreach $node_request ($nodeset->find('//dp:request')->get_nodelist){
		$node_request->setAttribute('domain',$domain);
	};
	foreach my $node ($nodeset->find('//dp:set-file')->get_nodelist) {
		$node->setAttribute('name',$name);
		my $encodedfile = XML::XPath::Node::Text->new($encoded);
		#$node->setValue($encodedfile);}
		$node->appendChild($encodedfile);
	}
	open(TMP,">".$xml_tmp);
	foreach my $node ($nodeset->find('/')->get_nodelist) {
		print TMP XML::XPath::XMLParser::as_string($node), "\n\n"; 
	}
	system($curl." -k -u ".$user.":".$pass." -d @".$xml_tmp." ".$RTC)==0 or die "no se pudo subir el archivo";
	
}

sub getLista_Archivos {
    my $path = shift;
    opendir (DIR, $path)
        or die "No se puede abrir el $path: $!";
    my @files = grep { !/^\.{1,2}$/ } readdir (DIR);
    closedir (DIR);
    @files = map {  $path ."\\".$_ } @files;
	
    for (@files) {
      if(-f $_){
	  
          if ($_=~/XML\\.*\..*$/){
			   print "\nsubiendo archivo: ".$_."\n";
               Crear_archivo_teporal($_,"xml");
              }
		  elsif($_=~/XSL\\.*\..*$/){
			 print "\nsubiendo archivo: ".$_."\n";
			 Crear_archivo_teporal($_,"xsl");
			 }
		  elsif($_=~/NBA_NBO\\.*\..*$/){
			 print "\nsubiendo archivo: ".$_."\n";
			 Crear_archivo_teporal($_,"NBA_NBO");
		  }
		  else{
		    print "\nsubiendo archivo: ".$_."\n";
			Crear_archivo_teporal($_,"");
		  }
        
      }      
        if  (-d $_) {          
            getLista_Archivos ($_);
        }
		$contador++;
    }
}

sub get_filename{
 my $filepath=shift;
 $pos=rindex($filepath,"\\");
 $filename=substr($filepath,$pos+1);
 return $filename;
}

sub ejecutar{
	getLista_Archivos($dir);
	$n=0;
	foreach my $arch (@xmls){
		$n=$n+1;
		Crear_archivo_teporal($arch,"xml");
		print "xml numero: ".$n." nombre: ".$arch."\n";
	}
	$n=0;
	foreach my $arch (@xsls){
		$n=$n+1;
		Crear_archivo_teporal($arch,"xsl");
		print "xsl numero: ".$n." nombre: ".$arch."\n";
	}
	
}
$contador=1;
getLista_Archivos($dir);
#print $curl." -k -u ".$user.":".$pass." -d @".$xml." ".$RTC;