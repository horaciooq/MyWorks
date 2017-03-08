###version 1.0
use Config::IniFiles;
use File::Basename;
use File::Copy::Recursive qw(fcopy);
use File::Copy qw(copy cp);
use Log::Log4perl;
use Cwd;
use POSIX qw(strftime);

#declaracio de archivo de configuracion
my $cfg = Config::IniFiles->new( -file => "C:\\Harvest-FF\\config\\conf_SAE.ini", -default => "default");
#Lectura de parametros y declaracion de variables
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
$numero_de_archivos_validos=0;
$extensiones=$cfg->val( $project, 'extensiones' );
@myextensions = split(",",$extensiones);
$sln;
@netfiles;
@varfiles;
@rutatmp;
@ruta;

#Ejecutamos tareas previas de descargar codigo, determinar el lenguaje y ver si hay archivos validos para escaneo
&DownloadCode();
procesar_archivos($route);
switch();

#Sacar si hay archivos validos
sub detectarArchivosValidos {
    my $path = shift;
    opendir (DIR, $path)
        or die "No se puede abrir el $path: $!";
    my @files = grep { !/^\.{1,2}$/ } readdir (DIR);
    closedir (DIR);
    @files = map {  $path ."\\".$_ } @files;
    for (@files) {
      if(-f $_){
        foreach $ext (@myextensions){
          $myregex='\.'.$ext.'$'; 
          if ($_=~/$myregex/){
               $numero_de_archivos_validos++;
              }
        }
      }      
        if  (-d $_) {          
            detectarArchivosValidos ($_);
        }
    }
}

#Descargar el codigo del paquete
sub DownloadCode{
  $comando = 'hco -b '. $broker .' -en "' . $project . '" -st "Desarrollo" -cp "' . $route . '" -op pc -vp \\'. 
    ' -br -replace all -bo -ced -usr FTFYBNTE -pw horacio -rm '. $ipadd .
    ' -rport '. $port .' -er C:\\Harvest-FF\\dfo\\remoto.dfo -s "*" -pf '. $package .' -po';
    print($comando);
   system($comando)==0 or die "Error";
}

#Determinar el tipo de codigo aqui exploramos los archivos y dependiendo el directorio es que determinamos si es .net o no
sub procesar_archivos {
	my $path = shift;#se guarda el directorio donde se descargo el codigo
    opendir (DIR, $path)
        or die "No se puede abrir el $path: $!";
    my @files = grep { !/^\.{1,2}$/ } readdir (DIR);
    closedir (DIR);
    @files = map {  $path ."\\".$_ } @files;
    for (@files) {
		if (-f $_){
				$pos=index($_,"SAECG");#encontramos la posision donde se encuentra la carpeta
				$str=substr($_,$pos);
				$pos2=index($str,"\\");
				$str2=substr($str,0,$pos2);
				if($str2 eq "SAECG" || $str2 eq "SAECGCom" || $str2 eq "SAECGDB"){
					push @varfiles, $_;
				}elsif($str2 eq "SAECGNet"){
					push @netfiles,$_;
				}
		}
        if  (-d $_) {          
            procesar_archivos ($_);
        }
    }   
}

#en caso de haber archivos validos se procesan en caso contrario se aprueba el paquete
sub switch {
	detectarArchivosValidos($route);
	if ($numero_de_archivos_validos>0){
		@netfiles;
		@varfiles;
		if (@netfiles>0 && @varfiles>0){
			print ("Existen Archivos .NET y VB6 incluidos\n");
			limpiar();
			Nettranslate();
			Normaltranslate();
			scan();
		}elsif(@netfiles >0 && @varfiles==0){
			print ("Unicamente se enconraro archivos .NET \n");
			limpiar();
			Nettranslate();
			scan();
		}elsif(@netfiles ==0 && @varfiles>0){
			print("Solamente se encuentran archivos de codigo VB6 y ASP Classic\n");
			limpiar();
			Normaltranslate();
			scan();
		}else{
			print("Ningun tipo de archivo compatible");
		}
	}else{
		print "\nno hay archivos validos el paquete sera aprobado\n";
		$comando="java -jar C:\\Harvest-FF\\lib\\Aprobador.jar -b ".$broker." -eh C:\\Harvest-FF\\dfo\\harvest.dfo -p ".$package." -f Aprobado";
      		system($comando)==0 or die ('\nError al aprobar');
      		$comando= "C:\\Harvest-FF\\lib\\DelTree.cmd ". $route;
      		system($comando)==0 or die("Los archivos no se pueden eliminar");
	}
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
        if ($_=~/\.sln$/){
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
  print "Limpiando proyecto: " . $project ."...\n";
  $comando = 'sourceanalyzer -b "' . &Remplazar($project) . '" -clean';
  system($comando) ==0 or die "Error al momento de limpiar el build " .$project;
}

#Traduccion SCA para archivos asp y vb6
sub Normaltranslate{
	  print "Iniciando la fase de traduccion...\n";
	  $comando = 'sourceanalyzer -64 -Xmx'. $memory . ' -Xss'. $memoryxss .
		  ' -b "' . &Remplazar($project) . '" "' . $route ."\\". $project .'"';
	  system($comando) == 0 or die "Error de traduccion";
}

#Traduce el codigo .NET
sub Nettranslate {
system("mkdir ".$route."\\tmp\\SAE_B\\FUENTES\\SAECGNet") ==0 or die "no se pudo crear el directorio";#creamos el directorio para la descarga
  #Descarga completo del codigo
  $comando = 'hco -b '. $broker .' -en ' . $project . ' -st Desarrollo -cp ' . $route . '\tmp\SAE_B\FUENTES\SAECGNet -op pc -vp \\SAE_B\\FUENTES\\SAECGNet' . 
    ' -br -replace all -to  -ced -eh C:\\Harvest-FF\\dfo\\harvest.dfo -rm '. $ipadd .
    ' -rport '. $port .' -er C:\\Harvest-FF\\dfo\\remoto.dfo -s "*"';
  system($comando) ==0 or die"Imposible realizar la descarga completa revisar el comando ejecutado"; 
	print($comando);

  #Cambio de atributos a escritura 
  $comando ='attrib -r "'. $route."\\tmp\\".$project. '\\*.*" /s'; 
  system($comando)==0 or die "No se pudieron cambiar los permisos";
  
  #borrado de proyecto basura
  $comando="del ".$route."\\tmp\\SAE_B\\FUENTES\\SAECGNet\\SAECOM_Cajeros.vbproj";
  system($comando)==0 or die "no se puede eliminar el archivo";

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
  
  #migracion de proyecto a 2010
  print "Migrando el proyecto a version 2010...\n";
  $comando='C:\\Harvest-FF\\lib\\devenv.cmd "'.$sln .'" /upgrade';
  system($comando) ==0 or die ("no se pudo migrar el proyecto a 2010");
  
  #Modificacion del web.config
  print "Modificando archivo web.config ...\n";
  $comando="java -jar C:\\Harvest-FF\\lib\\SAEwebconfigmodify.jar ".$route . "\\tmp\\SAE_B\\FUENTES\\SAECGNet\\SAECGNet\\web.config";
  system($comando)==0 or die "Error al modificar el web.config";
  
  
  #Verificacion de la compilacion
  print "Verificanco la compilacion del codigo...\n";
  foreach $proj (@projs ){
    $comando = 'C:\\Harvest-FF\\lib\\devenv.cmd "'.$sln .'" /Rebuild debug /PROJECT '.$proj;
    system($comando) == 0 or die  "Error de compilacion ";
  }
  
   #Translate:
  print "Iniciando la fase de traducción...\n";
  foreach $proj (@projs){
    $comando = 'sourceanalyzer -64 -Xmx'. $memory . ' -Xss'. $memoryxss .
        ' -b '  .$project . ' "C:\\Harvest-FF\\lib\\devenv.cmd" "' .$sln . '" /Rebuild debug /PROJECT "'. $proj.'"';	
    system($comando) == 0 or die "Fallo en el momento de la traduccion ver log de SCA para mas infomacion";
  }
}

sub scan {
#Scan:
  print "Iniciando la fase de escaneo...\n";
  $comando = 'sourceanalyzer -64 -Xmx'. $memory . ' -Xss'. $memoryxss .
      ' -b ' . $project . ' -scan -f ' . $route ."\\". $project . '\results.fpr';
  system($comando) == 0 or die "El Escaneo". $project ." ha fallado";

  #Geracion de archivos xml y pdf
  print 'Generando Reporte PDF\n';
  $comando = 'ReportGenerator -format xml -template harvest.xml -f ' . $route ."\\". $project .'\results.xml -source '. $route ."\\". $project .'\results.fpr';
  system($comando) == 0 or die "No se pudo crear el archivo xml";
  $comando = 'ReportGenerator -format pdf -template harvest.xml -f ' . $route ."\\". $project .'\results.pdf -source '. $route ."\\". $project .'\results.fpr';
  system($comando) == 0 or die "No se pudo crear el archivo pdf";

  #Carga de resultados para auditoria
  print "Cargando resultados en SSC...\n";
  $comando = 'fortifyclient -url "' . $url . '" -authtoken "' . $token. 
      '" uploadFPR -file "' . $route .'\\'. $project . 
      '\results.fpr" -project "' . $project . '" -version "' . $parcial . '"';
  system($comando) ==0 or die "no se pudieron cargar los resultados";
  
  #Ejecutamos el jar para verificar datos modificar forma y mandar e-mail  

  print "Verificacion de resultados\n";
  $comando="java -jar C:\\Harvest-FF\\lib\\Validador_de_resultados_Beta.jar " . $route ."\\".$project." ". $project . ' ' . $completo.' '.$parcial.' '.$package .' '.$email ;
  system($comando) == 0 or die  "No se pudo realizar la verificacion revisar verificar.log";
  
  #Respaldos de FPRS y borrado de carpetas  
  $comando="Copy \"". $route ."\\" .$project . "\\results.fpr\" \"C:\\Harvest-FF\\FPRS\\".$project.$fecha.".fpr\"";
  system($comando) == 0 or die "Imposible generar el respaldo";
  $comando= "C:\\Harvest-FF\\lib\\DelTree.cmd \"". $route."\"";
  system($comando) ==0 or die "Los archivos no se pueden eliminar";
}
