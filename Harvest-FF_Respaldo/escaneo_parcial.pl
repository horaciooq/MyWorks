###version 1.0 - primer release APOLO-Diana-Escaneos de codigo en general
###version 1.1 - segundo release se agrega SAE
###version 1.2 - tercer release limpieza agregado de NBXI y BEC
use Config::IniFiles;
use File::Basename;
use File::Copy::Recursive qw(fcopy);
use File::Copy qw(copy cp);
use Log::Log4perl;
use Cwd;
use POSIX qw(strftime);

# Iniciamos el Logger
$dirn=dirname(__FILE__);
my $log_conf = qq(
     log4perl.rootLogger              = DEBUG, LOG1
   log4perl.appender.LOG1           = Log::Log4perl::Appender::File
   log4perl.appender.LOG1.filename  = $dirn\\logs\\parcial.log
   log4perl.appender.LOG1.mode      = append
   log4perl.appender.LOG1.layout    = Log::Log4perl::Layout::PatternLayout
   log4perl.appender.LOG1.layout.ConversionPattern = %d %p %m %n
);
Log::Log4perl::init(\$log_conf);
my $logger = Log::Log4perl->get_logger();


#Lectura de parametros
$project = $ARGV[0];
$package = $ARGV[1];
$haruser= $ARGV[2];
#declaracio de archivo de configuracion
my $cfg = Config::IniFiles->new( -file => dirname(__FILE__) . "\\config\\conf.ini", -default => "default");
#Declaracion de variables golobales
@rutatmp;
@ruta;
$sln;
$vbproj;
$email;
$fecha= strftime '%Y%m%d%H%M%S', gmtime(); 
$extensiones="java,jsp,jspx,tag,tagx,tld,sql,cfm,php,phtml,aspx,ctp,pks,pkh,prc,pkb,xml,config,settings,properties,exe,inc,asp,vbscript,js,ini,bas,cls,vbs,frm,ctl,html,htm,xsd,wsdd,xmi,py,cfml,cfc,abap,xhtml,cpx,xcfg,jsff,as,mxml,cbl,cob,vb,ASP";
@myextensions = split(",",$extensiones);
$numero_de_archivos_validos;
$broker=$cfg->val($project,'broker');
$ipaddress=$cfg->val($project,'ipaddress');
$puerto=$cfg->val($project, 'puerto');
$memory=$cfg->val($project, 'Xmx');
$memoryxss=$cfg->val($project, 'Xss');
$url=$cfg->val( $project, 'url' );
$token=$cfg->val($project,'Token');
$completo=$cfg->val( $project, 'completo' );
$parcial=$cfg->val($project, 'parcial');
$route=dirname(__FILE__).'\\run\\'.$haruser;



#Obtener el e-mail
$getmail='echo select email from haruser where username=\''.$haruser.'\' | hsql -b '.$broker.' -eh '. dirname(__FILE__).'\\dfo\\harvest.dfo -nh -s';
$email=`$getmail`;

#metodo para ejecutar comandos y escribir en el log
sub Ejecucion{
  $com = $_[0];
  $err = $_[1];
  $info = $_[2];
  if(system($com)>0){
    die ($logger->error($err."\n".$com));
  }else{
    $logger->info($info."\n".$com);
  }
}
#Metodo para remplazar caracteres en blanco
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
#metodo para descargar codigo
sub DownloadCode{
	$comando = 'hco -b '. $broker.' -en "' . $project . '" -st Desarrollo -cp "' . $route . '" -op pc -vp \\'. 
    ' -br -replace all -bo  -ced -eh '.dirname(__FILE__).'\\dfo\\harvest.dfo -rm '. $ipaddress .
    ' -rport '. $puerto .' -er '. dirname(__FILE__) .'\\dfo\\remoto.dfo -s "*" -pf '. $package .' -po';

	&Ejecucion($comando,"Imposible realizar la descarga revisar el comando ejecutado","Descarga de Archvos Finalizada Proyecto: ".$project . " Paquete: ".$package);   
	&detectarArchivosValidos($route."\\".$project);
}

sub JavaScan{
  #Limpiar
  print "Limpiando proyecto: " .&Remplazar($project) ."...\n";
  $comando = 'sourceanalyzer -b "' . &Remplazar($project) . '" -clean';
  &Ejecucion($comando, "Error al momento de limpiar el build " .&Remplazar($project), "Limpieza del build ". Remplazar($project) . "terminada");
 
  #Traduccion SCA
  print "Iniciando la fase de traduccion...\n";
  $comando = 'sourceanalyzer -Xmx'. $memory . ' -Xss'. $memoryxss .
      ' -b ' . &Remplazar($project) . ' -source ' .$cfg->val( $project, 'jdk' ) . ' "' . $route ."\\". $project .'"';
  &Ejecucion($comando, "Fallo en el momento de la traduccion ver log de SCA para mas infomacion", "Traduccion de ".&Remplazar($project) ." Realizada con exito");

  #Escaneo SCA
  print "Iniciando la fase de escaneo...\n";
  $comando = 'sourceanalyzer -Xmx' . $memory . ' -Xss'. $memoryxss .
      ' -b ' . &Remplazar($project) . ' -scan -f "' . $route ."\\" .$project . '\results.fpr"';
  &Ejecucion($comando, "El Escaneo". $project ." ha fallado", "El escaneo". $project ." ha finalizado con exito");
  
  #Generar Reporte XML y PDF
  print "Generando Reporte PDF \n";
  $comando = 'ReportGenerator -format xml -template harvest.xml -f "' . $route ."\\". $project .'\results.xml" -source "'. $route ."\\". $project .'\results.fpr"';
  &Ejecucion($comando,"No se pudo crear el archivo xml","results.xml creado con exito");
  $comando = 'ReportGenerator -format pdf -template harvest.xml -f "' . $route ."\\". $project .'\results.pdf" -source "'. $route ."\\". $project .'\results.fpr"';
  &Ejecucion($comando,"No se pudo crear el archivo pdf","results.pdf creado con exito");

   #Carga de resultados para auditoria
  print "Cargando resultados en SSC...\n";
  $comando = 'fortifyclient -url "' . $url . '" -authtoken "' . $token. 
      '" uploadFPR -file "' . $route .'\\'. $project . 
      '\results.fpr" -project "' . $project . '" -version "' . $parcial. '"';
  &Ejecucion($comando, "no se pudieron cargar los resultados", "Carga de resultados a SSC completa");

  #Ejecutamos el jar para verificar datos modificar forma y mandar e-mail
  print "Verificacion de resultados \n";  
  $comando='java -jar '.dirname(__FILE__).'\lib\Validador_de_resultados_Beta.jar ' . $route ."\\ \"". $project . '" ' . $completo .' '.$parcial.' '.$package .' '.$email ;
  &Ejecucion($comando, "No se pudo realizar la verificacion revisar verificar.log", "Verificacion completada sin errores");

  #Respaldos de FPRS y borrado de carpetas  
  $comando="Copy \"". $route ."\\" .$project . "\\results.fpr\" \"". dirname(__FILE__)."\\FPRS\\".$project.$fecha.".fpr\"";
  &Ejecucion($comando, "Imposible generar el respaldo", "respaldo generado sin errores");
  $comando= dirname(__FILE__)."\\lib\\DelTree.cmd \"". $route."\"";
  &Ejecucion($comando, "Los archivos no se pueden eliminar", "archivos eliminados");
}

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

sub GeneralScan{
  #Limpiar
  print "Limpiando proyecto: " . $project ."...\n";
  $comando = 'sourceanalyzer -b "' . &Remplazar($project) . '" -clean';
  &Ejecucion($comando, "Error al momento de limpiar el build " .$project, "Limpieza del build ". $project . "terminada");
 
  #Traduccion SCA
  print "Iniciando la fase de traduccion...\n";
  $comando = 'sourceanalyzer -64 -Xmx'. $memory . ' -Xss'. $memoryxss .
      ' -b "' . &Remplazar($project) . '" "' . $route ."\\". $project .'"';
  &Ejecucion($comando, "Fallo en el momento de la traduccion ver log de SCA para mas infomacion", "Traduccion de ".$project ." Realizada con exito");

  #Escaneo SCA
  print "Iniciando la fase de escaneo...\n";
  $comando = 'sourceanalyzer -64 -Xmx' . $memory . ' -Xss'. $memoryxss .
      ' -b "' . &Remplazar($project) . '" -scan -f "' . $route ."\\" .$project . '\results.fpr"';
  &Ejecucion($comando, "El Escaneo". $project ." ha fallado", "El escaneo". $project ." ha finalizado con exito");
  
  #Generar Reporte XML y PDF
  print "Generando Reporte PDF \n";
  $comando = 'ReportGenerator -format xml -template harvest.xml -f "' . $route ."\\". $project .'\results.xml" -source "'. $route ."\\". $project .'\results.fpr"';
  &Ejecucion($comando,"No se pudo crear el archivo xml","results.xml creado con exito");
  $comando = 'ReportGenerator -format pdf -template harvest.xml -f "' . $route ."\\". $project .'\results.pdf" -source "'. $route ."\\". $project .'\results.fpr"';
  &Ejecucion($comando,"No se pudo crear el archivo pdf","results.pdf creado con exito");

   #Carga de resultados para auditoria
  print "Cargando resultados en SSC...\n";
  $comando = 'fortifyclient -url "' . $url . '" -authtoken "' . $token. 
      '" uploadFPR -file "' . $route .'\\'. $project . 
      '\results.fpr" -project "' . $project . '" -version "' . $parcial . '"';
  &Ejecucion($comando, "no se pudieron cargar los resultados", "Carga de resultados a SSC completa");

  #Ejecutamos el jar para verificar datos modificar forma y mandar e-mail
  print "Verificacion de resultados \n";  
  $comando='java -jar '.dirname(__FILE__).'\lib\Validador_de_resultados_Beta.jar ' . $route ."\\". $project . ' '.$project.' ' . $completo .' '.$parcial.' '.$package .' '.$email ;
  &Ejecucion($comando, "No se pudo realizar la verificacion revisar verificar.log", "Verificacion completada sin errores");

  #Respaldos de FPRS y borrado de carpetas  
  $comando="Copy \"". $route ."\\" .$project . "\\results.fpr\" \"". dirname(__FILE__)."\\FPRS\\".$project.$fecha.".fpr\"";
  &Ejecucion($comando, "Imposible generar el respaldo", "respaldo generado sin errores");
  $comando= dirname(__FILE__)."\\lib\\DelTree.cmd \"". $route."\"";
  &Ejecucion($comando, "Los archivos no se pueden eliminar", "archivos eliminados");
}

$logger->info("Iniciando Escaneo Usuario: ".$haruser." Proyecto: ".$project." Paquete: ".$package);
if($cfg->SectionExists ($project)){
  $language=$cfg->val($project, 'lenguaje');
  if($language eq 'Java'){
    &DownloadCode ();
    if($numero_de_archivos_validos>0){
      &JavaScan ();
    }else{
      print "\nNo hay archivos validos";
      $comando= dirname(__FILE__)."\\lib\\DelTree.cmd ". $route;
      &Ejecucion($comando, "Los archivos no se pueden eliminar", "archivos eliminados");
    }
  }
  elsif($language eq 'NET'){
	if ($project eq 'SAE_B'){
		$comando="perl C:\\Harvest-FF\\VSScripts\\SAE.pl ".$project ." ".$package." ".$email." ".$fecha." ".$haruser;
		system($comando) == 0 or die "no se puede ejecutar SAE.pl";
	}elsif($project eq 'usd_BEC_B'){
    	$comando="perl C:\\Harvest-FF\\VSScripts\\BEC.pl ".$project ." ".$package." ".$email." ".$fecha." ".$haruser;
		print $comando;
		system($comando) == 0 or die "no se puede ejecutar BEC_B.pl";
	}elsif($project eq 'usd_NBxI_B'){
		$comando="perl C:\\Harvest-FF\\VSScripts\\NBXI.pl ".$project ." ".$package." ".$email." ".$fecha." ".$haruser;
		system($comando) == 0 or die "no se puede ejecutar NBXI.pl";
	}
  }
  elsif($language eq 'Phyton' || $language eq 'PHP' || $language eq 'AJAX' || $language eq 'JavaScript' || $language eq 'VB6' || $language eq 'VBScript' || $language eq 'HTML' || $language eq 'JSP' || $language eq 'XML'){
    &DownloadCode ();
    if($numero_de_archivos_validos>0){
      &GeneralScan  ();
    }else{
      print "\nFortify no encontro archivos compatibles, el paquete sera aprobado\n";
      $comando="java -jar ".dirname(__FILE__)."\\lib\\Aprobador.jar -b ".$broker.' -eh '.dirname(__FILE__)."\\dfo\\harvest.dfo -p ".$package." -f Aprobado";
      system($comando)==0 or die ('\nError al aprobar');
      $comando= dirname(__FILE__)."\\lib\\DelTree.cmd ". $route;
      &Ejecucion($comando, "Los archivos no se pueden eliminar", "archivos eliminados");
    }
  }
}
else{
  die ('No Existe la Seccion para el proyecto"' . $project. '"');
}