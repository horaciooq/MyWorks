$project = $ARGV[0];
$package = $ARGV[1];
$user = $ARGV[2];
sub println
{
        print ((@_? join($/, @_) : $_), $/);
}

$getid='echo select  IDPROYECTO from hardescpaquete inner join harForm  ON hardescpaquete.FORMOBJID = harForm.FORMOBJID where harForm.formname=\''.$package.'\' | hsql -b svrtransferad -usr harvest -pw ges_ver01 -nh -s';
$idproyecto=`$getid`;
println $project;
println $package;
println $user;
println $idproyecto;





