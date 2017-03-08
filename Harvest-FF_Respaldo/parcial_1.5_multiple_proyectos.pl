use Config::IniFiles;
use File::Basename;
use File::Copy::Recursive qw(fcopy);
use File::Copy qw(copy cp);
use Log::Log4perl;
use Cwd;

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

#Declaracion de variables golobales
@rutatmp;
@ruta;
$sln;
@projs;
$email;

#declaracio de archivo de configuracion
my $cfg = Config::IniFiles->new( -file => dirname(__FILE__) . "\\config\\conf.ini", -default => "default");

#Lectura de parametros
($proy) = $ARGV[0];
($pack) = $ARGV[1];
($haruser)= $ARGV[2];

#Obtener el e-mail
if ($haruser eq 'harvest'){
    $email="horacio.ochoa_smarts\@banorte.com";
}else{
  $email=system('echo select email from haruser where username=\''.$haruser.'\' | hsql -b svrtransferad -eh '. dirname(__FILE__).'\\dfo\\harvest.dfo -nh -s') == 0 or die ("FATAL ERROR");
}

#metodo para ejecutar comandos y escribir en el log
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

#metodo para descargar codigo
sub DownloadCode{
  $project = $_[0];
  $package = $_[1];

  $comando = 'hco -b '. $cfg->val($project, 'broker') .' -en ' . $project . ' -st Desarrollo -cp ' . $route . ' -op pc -vp \\'. 
    ' -br -replace all -bo  -ced -eh '.dirname(__FILE__).'\\dfo\\harvest.dfo -rm '. $cfg->val($project, 'ipaddress' ) .
    ' -rport '. $cfg->val($project, 'puerto') .' -er '. dirname(__FILE__) .'\\dfo\\remoto.dfo -s "*" -pf '. $package .' -po';

   &Ejecucion($comando,"Imposible realizar la descarga revisar el comando ejecutado","Descarga de Archvos Finalizada Proyecto: ".$project . " Paquete: ".$package); 
}

sub JavaScan{
  $project = $_[0];
  $package = $_[1];

  #Limpiar
  print "Limpiando proyecto: " . $project ."...\n";
  $comando = 'sourceanalyzer -b "' . $project . '" -clean';
  &Ejecucion($comando, "Error al momento de limpiar el build " .$project, "Limpieza del build ". $project . "terminada");
 
  #Traduccion SCA
  print "Iniciando la fase de traducción...\n";
  $comando = 'sourceanalyzer -64 -Xmx'. $cfg->val($project, 'Xmx') . ' -Xss'. $cfg->val($project, 'Xss') .
      ' -b ' . $project . ' -source ' .$cfg->val( $project, 'jdk' ) . ' "' . $route ."\\". $project .'"';
  &Ejecucion($comando, "Fallo en el momento de la traduccion ver log de SCA para mas infomacion", "Traduccion de ".$project ." Realizada con exito");

  #Escaneo SCA
  print "Iniciando la fase de escaneo...\n";
  $comando = 'sourceanalyzer -64 -Xmx' . $cfg->val($project, 'Xmx') . ' -Xss'. $cfg->val($project, 'Xss') .
      ' -b ' . $project . ' -scan -f "' . $route ."\\" .$project . '\results.fpr"';
  &Ejecucion($comando, "El Escaneo". $project ." ha fallado", "El escaneo". $project ." ha finalizado con exito");
  
  #Generar Reporte XML y PDF
  print 'Generando Reporte XML';
  $comando = 'ReportGenerator -format xml -template harvest.xml -f ' . $route ."\\". $project .'\results.xml -source '. $route ."\\". $project .'\results.fpr';
  &Ejecucion($comando,"No se pudo crear el archivo xml","results.xml creado con exito");
  $comando = 'ReportGenerator -format pdf -template harvest.xml -f ' . $route ."\\". $project .'\results.pdf -source '. $route ."\\". $project .'\results.fpr';
  &Ejecucion($comando,"No se pudo crear el archivo pdf","results.pdf creado con exito");

  #Ejecutamos el jar para verificar datos modificar forma y mandar e-mail
  print "Verificacion de resultados \n";  
  $comando='java -jar '.dirname(__FILE__).'\lib\Validador_de_resultados.jar ' . $route ."\\ ". $project . ' ' . $cfg->val( $project, 'version' ) .' '.$package .' '.$email ;
  &Ejecucion($comando, "No se pudo realizar la verificacion revisar verificar.log", "Verificacion completada sin errores");
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
        if($_=~/\.vbproj$/ || $_=~/\.csproj$/){
          $pos=rindex($_,'\\');
          #$size=length($_);
          $str=substr($_,0,$pos);
          push @ruta,$str;
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
          $locacion=substr($_,length($route)+4);
          $directorios=$route.$locacion;
          copy $_,$directorios;
        }

        if  (-d $_) {
          $locacion=substr($_,length($route)+4);
          $directorios=$route.$locacion;
          push @rutatmp,$directorios;
          process_files2 ($_);
        }
    }
}

sub VSScan{
  $project = $_[0];
  $package = $_[1];

  #Descarga completo del codigo
  $comando = 'hco -b '. $cfg->val($project, 'broker') .' -en ' . $project . ' -st Desarrollo -cp ' . $route . ' -op pc -vp \\' . 
    ' -br -replace all -to  -ced -eh '. dirname(__FILE__).'\\dfo\\harvest.dfo -rm '. $cfg->val($project, 'ipaddress' ) .
    ' -rport '. $cfg->val($project, 'puerto') .' -er '.dirname(__FILE__)  .'\\dfo\\remoto.dfo -s "*"';
  &Ejecucion($comando,"Imposible realizar la descarga completa revisar el comando ejecutado","Descarga de Archvos Finalizada Proyecto: ".$project); 

   $comando = 'hco -b '. $cfg->val($project, 'broker') .' -en ' . $project . ' -st Desarrollo -cp ' . $route  . '\\tmp -op pc -vp \\' . 
    ' -br -replace all -bo  -ced -eh '. dirname(__FILE__).'\\dfo\\harvest.dfo -rm '. $cfg->val($project, 'ipaddress' ) .
    ' -rport '. $cfg->val($project, 'puerto') .' -er '. dirname(__FILE__) .'\\dfo\\remoto.dfo -s "*" -pf '. $package .' -po';
  &Ejecucion($comando,"Imposible realizar la descarga revisar el comando ejecutado","Descarga de Archvos Finalizada Proyecto: ".$project . " Paquete: ".$package); 
 
  #Cambio de atributos a escritura 
  $comando ='attrib -r "'. $route."\\".$project. '\\*.*" /s'; 
  &Ejecucion($comando,"No se pudieron cambiar los permisos", "Permisos cambiados conexito");

  #Obtension del sln y vbproj
  process_files($route);
  process_files2($route.'\\tmp');
  foreach my $n (@ruta) {
     foreach my $no (@rutatmp){
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

 
  
  print "Verificanco la compilación del codigo...\n";
  foreach $proj (@projs ){
    $comando = '"'.$cfg->val($project, 'VSpath').'devenv.exe" '.$sln .' /Rebuild debug /PROJECT '.$proj;
    &Ejecucion($comando, "Error de compilacion " .$project, "Compilacion completa");
  }
  
  #Limpiar:
  print "Limpiando proyecto: " . $project ."... \n";
  $comando = 'sourceanalyzer -b ' .$project . ' -clean';
  &Ejecucion($comando, "Error al momento de limpiar el build " .$project, "Limpieza del build ". $project . "terminada");

  #Translate:
  print "Iniciando la fase de traducción...\n";
  foreach $proj (@projs){
    $comando = 'sourceanalyzer -64 -Xmx'. $cfg->val($project, 'Xmx') . ' -Xss'. $cfg->val($project, 'Xss') .
        ' -b '  .$project . ' "'.$cfg->val($project, 'VSpath').'devenv.exe" '  .$sln . ' /Rebuild debug /PROJECT '. $proj;
    &Ejecucion($comando, "Fallo en el momento de la traduccion ver log de SCA para mas infomacion", "Traduccion de ".$project ." Realizada con exito");
  }

  #Scan:
  print "Iniciando la fase de escaneo...\n";
  $comando = 'sourceanalyzer -64 -Xmx'. $cfg->val($project, 'Xmx') . ' -Xss'. $cfg->val($project, 'Xss') .
      ' -b ' . $project . ' -scan -f ' . $route ."\\". $project . '\results.fpr';
  &Ejecucion($comando, "El Escaneo". $project ." ha fallado", "El escaneo". $project ." ha finalizado con exito");

  #Geracion de archivos xml y pdf
  print 'Generando Reporte PDF';
  $comando = 'ReportGenerator -format xml -template harvest.xml -f ' . $route ."\\". $project .'\results.xml -source '. $route ."\\". $project .'\results.fpr';
  &Ejecucion($comando,"No se pudo crear el archivo xml","results.xml creado con exito");
  $comando = 'ReportGenerator -format pdf -template harvest.xml -f ' . $route ."\\". $project .'\results.pdf -source '. $route ."\\". $project .'\results.fpr';
  &Ejecucion($comando,"No se pudo crear el archivo pdf","results.pdf creado con exito");

  print 'Verificacion de resultados';
  $comando='java -jar C:\Scripts\Validador_de_resultados.jar ' . $route ."\\ " . $project . ' ' . $cfg->val( $project, 'version' ) .' '.$package .' '.$email ;
  &Ejecucion($comando, "No se pudo realizar la verificacion revisar verificar.log", "Verificacion completada sin errores");
}

$route=dirname(__FILE__).'\\run\\'.$haruser;
$user=$cfg->val( $proy, 'usuario');
$password=$cfg->val( $proy, 'contrasena');

$logger->info("Iniciando Escaneo Usuario: ".$haruser." Proyecto: ".$proy." Paquete: ".$pack);
if($cfg->SectionExists ($proy)){
  $language=$cfg->val($proy, 'lenguaje');
  if($language eq 'Java'){
    &DownloadCode ($proy,$pack);
    &JavaScan ($proy, $pack);
  }
  elsif($language eq 'NET'){
    #&DownloadCode ($proy,$pack);
    &VSScan ($proy,$pack);
  }
  elsif($language eq 'ASP'){
    #&DownloadCode ($proy,$pack);
    &ASPScan ($proy);
  }
  elsif($language eq 'Phyton' || $language eq 'PHP' || $language eq 'AJAX' || $language eq 'JavaScript' || $language eq 'VB6' || $language eq 'VBScript' || $language eq 'HTML' || $language eq 'JSP' || $language eq 'XML'){
    #&DownloadCode ($proy,$pack);
    &GeneralScan ($proy);
  }
}
else{
  die ('No Existe la Seccion para el proyecto"' . $proy. '"');
}