#script de escaneo fortify
use Config::IniFiles;
#variable pasada por linea de comandos
$codepath=$ARGV[0];#TARGETROOT
#definicion de archivos de propiedades
my $cfg = Config::IniFiles->new( -file => "C:\\Harvest-FF\\RTC\\conf\\conf.ini" ,-default=>"default");
#print('xcopy /y/r/i '.$codepath."\\fortify-properties.ini C:\\Harvest-FF\\RTC\\conf\\" );
#system('xcopy /y/r/i '.$codepath."\\fortify-properties.ini C:\\Harvest-FF\\RTC\\conf\\");
my $fortifycfg=Config::IniFiles->new( -file => $codepath."\\fortify-properties.ini",-default=>"default");
#declarar variables
$memoria=$fortifycfg->val('default','memoria');
$buildid=$fortifycfg->val('default','buildid');
$source=$fortifycfg->val('default','source');
$proyecto=$fortifycfg->val('default','aplicacion');
$version=$fortifycfg->val('default','folio');
$classpath=$codepath; #debo de determinarlo;
#declarar estaticas
$directorypath=$cfg->val('default','directorio');
$token=$cfg->val('default','token');
$linea_base=$cfg->val('default','linea_base');
$url_ssc=$cfg->val('default','sscurl');
$xml_destpath=$directorypath."\\XMLS\\".$buildid.".xml";
$fpr_file=$directorypath."\\FPRS\\".$buildid.".fpr";
$resultados_file=$directorypath."\\Resultados\\results.txt";




#clean
system("sourceanalyzer -Xmx".$memoria." -b ".$buildid." -clean");
#translate 
system("sourceanalyzer -Xmx".$memoria." -b ".$buildid." -source ".$source." \"".$codepath."\" -cp \"".$classpath);
#scan
system("sourceanalyzer -Xmx".$memoria." -b ".$buildid." -scan -f ".$fpr_file);
#subir resultados a SSC
system("fortifyclient -url ". $url_ssc." -authtoken ". $token." uploadFPR -file \"".$fpr_file."\" -project ".$proyecto." -version ".$version);
#generar xml y copiarlo
system('ReportGenerator -format xml -template harvest.xml -f "' .$xml_destpath.'" -source "'. $fpr_file.'"');
#verificar resultados
print('java -jar '.$directorypath.'\Scripts\Revision_resultados.jar '.$xml_destpath.' '.$proyecto.' '.$linea_base.' '. $version.' '.$resultados_file);
system('java -jar '.$directorypath.'\Scripts\Revision_resultados.jar '.$xml_destpath.' '.$proyecto.' '.$linea_base.' '. $version.' '.$resultados_file);
print('xcopy /y/e/r/i '.$resultados_file.' '. $codepath);
system('xcopy /y/e/r/i '.$resultados_file.' '. $codepath);