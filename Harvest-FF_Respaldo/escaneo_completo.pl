use Config::IniFiles;
use File::Basename;
use File::Path;
use Log::Log4perl;

# Iniciamos el Logger
$dirn=dirname(__FILE__);
my $log_conf = qq(
     log4perl.rootLogger              = DEBUG, LOG1
   log4perl.appender.LOG1           = Log::Log4perl::Appender::File
   log4perl.appender.LOG1.filename  = $dirn\\logs\\completo.log
   log4perl.appender.LOG1.mode      = append
   log4perl.appender.LOG1.layout    = Log::Log4perl::Layout::PatternLayout
   log4perl.appender.LOG1.layout.ConversionPattern = %d %p %m %n
);
Log::Log4perl::init(\$log_conf);
my $logger = Log::Log4perl->get_logger();
$sln;

sub Ejecucion{
  $com = $_[0];
  $err = $_[1];
  $info = $_[2];
  if(system($com)>0){
    die ($logger->error($err."\n".$com));
  }else{
    $logger->info($info);
  }
}

my $cfg = Config::IniFiles->new( -file => dirname(__FILE__) . "\\config\\conf.ini", -default => "default");
  $project = $ARGV[0];
  $package = $ARGV[1];
  $haruser=  $ARGV[2];
sub DownloadCode{
  $comando = 'hco -b '. $cfg->val($project, 'broker') .' -en ' . $project . ' -st Desarrollo -cp ' . $route . ' -op pc -vp \\'.
    ' -br -replace all -to  -ced -eh ' .dirname(__FILE__).'\\dfo\\harvest.dfo -rm '. $cfg->val($project, 'ipaddress' ) .
    ' -rport '. $cfg->val($project, 'puerto') .' -er ' . dirname(__FILE__). '\\dfo\\remoto.dfo -s "*"';
  &Ejecucion($comando,"Imposible realizar la descarga revisar el comando ejecutado","Descarga de Archvos Finalizada Proyecto: ".$project . " Paquete: ".$package); 

  $comando = 'hco -b '. $cfg->val($project, 'broker') .' -en ' . $project . ' -st Desarrollo -cp ' . $route . ' -op pc -vp \\' .
    ' -br -replace all -bo  -ced -eh '. dirname(__FILE__).'\\dfo\\harvest.dfo -rm '. $cfg->val($project, 'ipaddress' ) .
    ' -rport '. $cfg->val($project, 'puerto') .' -er '. dirname(__FILE__).'\\dfo\\remoto.dfo -s "*" -pf '. $package .' -po';
  &Ejecucion($comando,"Imposible realizar la descarga revisar el comando ejecutado","Descarga de Archvos Finalizada Proyecto: ".$project . " Paquete: ".$package);   

}

#Subrutina para escanear codigo fuente en Java 
sub JavaScan{

  #Limpiar
  print "Limpiando proyecto: " . $project ."...\n";
  $comando = 'sourceanalyzer -b "' . $project . '" -clean';
  &Ejecucion($comando, "Error al momento de limpiar el build " .$project, "Limpieza del build ". $project . "terminada");
  
  print "Iniciando la fase de traducción...\n";
  $comando = 'sourceanalyzer -64 -Xmx'. $cfg->val($project, 'Xmx') . ' -Xss'. $cfg->val($project, 'Xss') .
      ' -b ' . $project . ' -source ' .$cfg->val( $project, 'jdk' ) . ' "' . $route .'\\'. $project .'"';
  &Ejecucion($comando, "Fallo en el momento de la traduccion ver log de SCA para mas infomacion", "Traduccion de ".$project ." Realizada con exito");


  print "Iniciando la fase de escaneo...\n";
  $comando = 'sourceanalyzer -64 -Xmx' . $cfg->val($project, 'Xmx') . ' -Xss'. $cfg->val($project, 'Xss') .
      ' -b ' . $project . ' -scan -f "' . $route .'\\'. $project . '\results.fpr"';
  &Ejecucion($comando, "El Escaneo". $project ." ha fallado", "El escaneo". $project ." ha finalizado con exito");
  
  print "Cargando resultados en SSC...\n";
  $comando = 'fortifyclient -url "' . $cfg->val( $project, 'url' ) . '" -authtoken "' . $cfg->val($project,'Token'). 
      '" uploadFPR -file "' . $route .'\\'. $project . 
      '\results.fpr" -project "' . $project . '" -version "' . $cfg->val( $project, 'version' ) . '"';
  &Ejecucion($comando, "no se pudieron cargar los resultados", "Carga de resultados a SSC completa");
  
  print "Obteniendo respuesta del SSC...\n";
  $JavaExe = 'java -jar '. dirname(__FILE__).'\\lib\\connector.jar "' . $cfg->val($project, 'version' ) . '" "' . $project . '" "' . $package .'"' ;
  sleep 30;
  &Ejecucion($JavaExe,"No se pudo obtener respuesta de SSC favor de revisar conector.log para mas info","El aplicativo de escaneo correctamente");
  #borrar archivos que se descargaron y crearon despues de finalizar
  print "Borrando archivos...";
  rmtree( $route, 1, 1);
}

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
        if  (-d $_) {          
            process_files ($_);
        }
    }
}

#Subrutina para escanear codigo fuente .NET
sub VSScan{
  process_files($route."\\".$project);

  #Cambio de atributos a escritura 
  $comando ='attrib -r "'. $route."\\".$project. '\\*.*" /s'; 
  &Ejecucion($comando,"No se pudieron cambiar los permisos", "Permisos cambiados conexito");

  #Verificar la Compilacion
  print "Verificanco la compilación del codigo...\n";
  $comando = '"'.$cfg->val($project, 'VSpath').'devenv.exe" ' .$sln .' /Rebuild debug';
  &Ejecucion($comando, "Error de compilacion " .$project, "Compilacion completa");

  #Limpiar:
  print "Limpiando proyecto: " . $project ."... \n";
  $comando = 'sourceanalyzer -b ' .$project . ' -clean';
  &Ejecucion($comando, "Error al momento de limpiar el build " .$project, "Limpieza del build ". $project . "terminada");

  #Translate:
  print "Iniciando la fase de traducción...\n";
  $comando = 'sourceanalyzer -64 -Xmx'. $cfg->val($project, 'Xmx') . ' -Xss'. $cfg->val($project, 'Xss') .
      ' -b '  .$project . ' "'.$cfg->val($project, 'VSpath').'devenv.exe" ' .$sln .
              ' /Rebuild debug';
  &Ejecucion($comando, "Fallo en el momento de la traduccion ver log de SCA para mas infomacion", "Traduccion de ".$project ." Realizada con exito");

  #Scan:
  print "Iniciando la fase de escaneo...\n";
  $comando = 'sourceanalyzer -64 -Xmx'. $cfg->val($project, 'Xmx') . ' -Xss'. $cfg->val($project, 'Xss') .
      ' -b ' . $project . ' -scan -f ' . $route ."\\". $project . '\results.fpr';
  &Ejecucion($comando, "El Escaneo". $project ." ha fallado", "El escaneo". $project ." ha finalizado con exito");

  #Subir Resultados:
  print "Cargando resultados en SSC...\n";
  $comando = 'fortifyclient -url "' . $cfg->val( $project, 'url' ) . '" -authtoken "' . $cfg->val($project,'Token'). 
      '" uploadFPR -file "' . $route ."\\". $project . 
      '\results.fpr" -project "' . $project . '" -version "' . $cfg->val( $project, 'version' ) . '"';
  &Ejecucion($comando, "no se pudieron cargar los resultados", "Carga de resultados a SSC completa");
  
  print "Obteniendo respuesta del SSC...\n";
  $JavaExe = 'java -jar C:\scripts\connector.jar "' . $cfg->val($project, 'version' ) . '" "' . $project . '" "' . $package .'"' ;
  sleep 30;
  &Ejecucion($JavaExe,"No se pudo obtener respuesta de SSC favor de revisar conector.log para mas info","El aplicativo de escaneo correctamente");

  #borrar archivos que se descargaron y crearon despues de finalizar
  print "Borrando archivos...";
  rmtree( $route, 1, 1);
}

#Subrutina para escanear codigo fuente en General
sub GeneralScan{

  #Limpiar
  print "Limpiando proyecto: " . $project ."...\n";
  $comando = 'sourceanalyzer -b "' . $project . '" -clean';
  &Ejecucion($comando, "Error al momento de limpiar el build " .$project, "Limpieza del build ". $project . "terminada");
  
  print "Iniciando la fase de traducción...\n";
  $comando = 'sourceanalyzer -64 -Xmx'. $cfg->val($project, 'Xmx') . ' -Xss'. $cfg->val($project, 'Xss') .
      ' -b ' . $project . ' "' . $route .'\\'. $project .'"';
  &Ejecucion($comando, "Fallo en el momento de la traduccion ver log de SCA para mas infomacion", "Traduccion de ".$project ." Realizada con exito");


  print "Iniciando la fase de escaneo...\n";
  $comando = 'sourceanalyzer -64 -Xmx' . $cfg->val($project, 'Xmx') . ' -Xss'. $cfg->val($project, 'Xss') .
      ' -b ' . $project . ' -scan -f "' . $route .'\\'. $project . '\results.fpr"';
  &Ejecucion($comando, "El Escaneo". $project ." ha fallado", "El escaneo". $project ." ha finalizado con exito");
  
  print "Cargando resultados en SSC...\n";
  $comando = 'fortifyclient -url "' . $cfg->val( $project, 'url' ) . '" -authtoken "' . $cfg->val($project,'Token'). 
      '" uploadFPR -file "' . $route .'\\'. $project . 
      '\results.fpr" -project "' . $project . '" -version "' . $cfg->val( $project, 'version' ) . '"';
  &Ejecucion($comando, "no se pudieron cargar los resultados", "Carga de resultados a SSC completa");
  
  print "Obteniendo respuesta del SSC...\n";
  $JavaExe = 'java -jar '. dirname(__FILE__).'\\lib\\connector.jar "' . $cfg->val($project, 'version' ) . '" "' . $project . '" "' . $package .'"' ;
  sleep 30;
  &Ejecucion($JavaExe,"No se pudo obtener respuesta de SSC favor de revisar conector.log para mas info","El aplicativo de escaneo correctamente");
  #borrar archivos que se descargaron y crearon despues de finalizar
  print "Borrando archivos...";
  rmtree( $route, 1, 1);
}

#Lectura de Archivo de configuración 

$route=dirname(__FILE__)."\\run\\".$haruser;

if($cfg->SectionExists ($project)){
  $language=$cfg->val($project, 'lenguaje');
  if($language eq 'Java'){
    &DownloadCode();
    &JavaScan();
  }
  elsif($language eq 'NET'){
    &DownloadCode ($proy,$pack);
    &VSScan();
  }
  elsif($language eq 'ASP'){
    #&DownloadCode ($proy,$pack);
    &ASPScan ();
  }
  elsif($language eq 'Phyton' || $language eq 'PHP' || $language eq 'AJAX' || $language eq 'JavaScript' || $language eq 'VB6' || $language eq 'VBScript' || $language eq 'HTML' || $language eq 'JSP' || $language eq 'XML'){
    &DownloadCode ();
    &GeneralScan ();
  }
}
else{
  die ('No Existe la Seccion para el proyecto"' . $proy. '"');
}