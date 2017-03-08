###version 1.0
use Config::IniFiles;
use File::Basename;
use File::Copy::Recursive qw(fcopy);
use File::Copy qw(copy cp);
use Log::Log4perl;
use Cwd;
use POSIX qw(strftime);

#declaracio de archivo de configuracion
my $cfg = Config::IniFiles->new( -file => "C:\\Harvest-FF\\config\\conf.ini", -default => "default");

#Lectura de parametros
$project = $ARGV[0];
$package = $ARGV[1];
$email = $ARGV[2];
$fecha=$ARGV[3];
$user=$ARGV[4];
$broker = $cfg->val($project,'broker');
$ipadd = $cfg->val($project, 'ipaddress' );
$port=$cfg->val($project, 'puerto');
$route="C:\\Harvest-FF\\run\\".$user;
$memory=$cfg->val($project, 'Xmx');
$memoryxss=$cfg->val($project, 'Xss');
$url=$cfg->val( $project, 'url' );
$token=$cfg->val($project,'Token');
$completo=$cfg->val($project,'completo');
$parcial=$cfg->val( $project, 'parcial' );
$sln;
@netfiles;
@varfiles;
@rutatmp;
@ruta;
&DownloadCode();
limpiar();
Nettranslate();
scan();

#Descargar el codigo del paquete
sub DownloadCode{
  $comando = 'hco -b '. $broker .' -en "' . $project . '" -st "Desarrollo" -cp "' . $route . '" -op pc -vp \\'. 
    ' -br -replace all -bo -ced -usr FTFYBNTE -pw horacio -rm '. $ipadd .
    ' -rport '. $port .' -er C:\\Harvest-FF\\dfo\\remoto.dfo -s "*" -pf '. $package .' -po';
   #system($comando)==0 or die "Error";
}

#Funcion para Remplazar los nombre del proyecto en caso de tener espacios en blanco
sub Remplazar{
  $string  = $_[0];
  $find    = " ";
  $replace = "_";

  $pos = index($string, $find);
  while ( $pos > -1 ) {
    substr( $string, $pos, length( $find ), $replace );
    $pos = index( $string, $find, $pos + length( $replace ));
  }
  return $string;
}

#Metodo usados para leer lo archivos y determinar el SLN los archivos modificados y el proyecto al que pertenecen  
sub process_files {
    my $path = shift;
    opendir (DIR, $path)
        or die "No se puede abrir el $path: $!";
    my @files = grep { !/^\.{1,2}$/ } readdir (DIR);
    closedir (DIR);
    @files = map {  $path ."\\".$_ } @files;
    for (@files) {
        if ($_=~/\.NBXI.sln$/){
            $sln=$_;
        }
        if($_=~/\.vbproj$/ || $_=~/\.csproj$/){
          $pos=rindex($_,'\\');
          $str=substr($_,0,$pos);
          push @rutatmp,$str;
        }       
        if  (-d $_) {          
            process_files ($_);
        }
    }
}

sub process_files2 {
    my $path = shift; 
    opendir (DIR, $path)
        or die "Unable to open $path: $!";
    my @files = grep { !/^\.{1,2}$/ } readdir (DIR);
    closedir (DIR);
    @files = map {  $path."\\" .$_ } @files;
    for (@files) {
        if (-f $_){
		
          $locacion=substr($_,length($route));
          $directorios=$route.'\\tmp\\'.$locacion;
          copy $_,$directorios;
        }

        if  (-d $_) {
          $locacion=substr($_,length($route));
          $directorios=$route.'\\tmp'.$locacion;
          push @ruta,$directorios;
          process_files2 ($_);
        }
    }
}

#Limpia el proyecto de Fortify
sub limpiar{
  print "\nLimpiando proyecto: " . $project ."...\n";
  $comando = 'sourceanalyzer -b "' . &Remplazar($project) . '" -clean';
  system($comando) ==0 or die "Error al momento de limpiar el build " .$project;
}

#Traduce el codigo .NET
sub Nettranslate {
#system("mkdir ".$route."\\tmp\\") ==0 or die "no se pudo crear el directorio";#creamos el directorio para la descarga
  #Descarga completo del codigo
  $comando = 'hco -b '. $broker .' -en ' . $project . ' -st Desarrollo -cp ' . $route . '\tmp\ -op pc -vp \\' . 
    ' -br -replace all -to  -ced -eh C:\\Harvest-FF\\dfo\\harvest.dfo -rm '. $ipadd .
    ' -rport '. $port .' -er C:\\Harvest-FF\\dfo\\remoto.dfo -s "*"';
  #system($comando) ==0 or die"Imposible realizar la descarga completa revisar el comando ejecutado"; 

  #Cambio de atributos a escritura 
  $comando ='attrib -r "'. $route.'\\tmp\\*.*" /s'; 
  system($comando)==0 or die "No se pudieron cambiar los permisos";

  #Obtension del sln y vbproj
  process_files($route.'\\tmp');
  process_files2($route);
  foreach my $n (@rutatmp) {
     foreach my $no (@ruta){
        if ($no eq $n){
          opendir(DIR, $n);
          @FILES = readdir(DIR);
          foreach $file (@FILES) {
            if ($file=~ /\.vbproj$/ || $file=~/\.csproj$/){
              push @projs,$n."\\".$file;
            }
          }
          closedir(DIR);
        }
    }
  }
 
  #Verificacion de la compilacion
  print "Verificanco la compilacion del codigo...\n";
  foreach $proj (@projs ){
    $comando = 'C:\\Harvest-FF\\lib\\devenv.cmd "'.$sln .'" /Rebuild debug /PROJECT '.$proj;
	print $comando."\n";
    system($comando) == 0 or die  "Error de compilacion ";
	print "pausa de 2 min";
  }
  
   #Translate:
  print "Iniciando la fase de traducción...\n";
  foreach $proj (@projs){
    $comando = 'sourceanalyzer -Xmx'. $memory . ' -Xss'. $memoryxss .
        ' -b '  .$project . ' "C:\\Harvest-FF\\lib\\devenv.cmd" "' .$sln . '" /Rebuild debug /PROJECT "'. $proj.'"';
	print $comando;	
    system($comando) == 0 or die "Fallo en el momento de la traduccion ver log de SCA para mas infomacion";
	print "hasta aqui no hay fallos";
  }
}

sub scan {
#Scan:
  print "Iniciando la fase de escaneo...\n";
  $comando = 'sourceanalyzer -64 -Xmx'. $memory . ' -Xss'. $memoryxss .
      ' -b ' . $project . ' -scan -f ' . $route ."\\BEC_B\\results.fpr";
  system($comando) == 0 or die "El Escaneo". $project ." ha fallado";

  #Geracion de archivos xml y pdf
  print 'Generando Reporte PDF\n';
  $comando = 'ReportGenerator -format xml -template harvest.xml -f ' . $route ."\\BEC_B\\results.xml -source ". $route ."\\BEC_B\\results.fpr";
  system($comando) == 0 or die "No se pudo crear el archivo xml";
  $comando = 'ReportGenerator -format pdf -template harvest.xml -f ' . $route ."\\BEC_B\\results.pdf -source ". $route ."\\BEC_B\\results.fpr";
  system($comando) == 0 or die "No se pudo crear el archivo pdf";

  #Carga de resultados para auditoria
  print "Cargando resultados en SSC...\n";
  $comando = 'fortifyclient -url "' . $url . '" -authtoken "' . $token. 
      '" uploadFPR -file "' . $route .'\\BEC_B\\results.fpr" -project "' . $project . '" -version "' . $parcial . '"';
  system($comando) ==0 or die "no se pudieron cargar los resultados";
  
  #Ejecutamos el jar para verificar datos modificar forma y mandar e-mail  
  print "Verificacion de resultados\n";
  $comando="java -jar C:\\Harvest-FF\\lib\\Validador_de_resultados_Beta.jar " . $route ."\\BEC_B" . ' ' . $completo.' '.$parcial.' '.$package .' '.$email ;
  system($comando) == 0 or die  "No se pudo realizar la verificacion revisar verificar.log";
  
  #Respaldos de FPRS y borrado de carpetas  
  $comando="Copy \"". $route ."\\BEC_B\\results.fpr\" \"C:\\Harvest-FF\\FPRS\\".$project.$fecha.".fpr\"";
  system($comando) == 0 or die "Imposible generar el respaldo";
  $comando= "C:\\Harvest-FF\\lib\\DelTree.cmd \"". $route."\"";
  #system($comando) ==0 or die "Los archivos no se pueden eliminar";
}
