@echo off


set lscm=call "%RTC_HOME%\scmtools\eclipse\lscm.bat"
set ant=call perl C:\DOCUME~1\ADMINI~1\MISDOC~1\INSTAL~1\DDS-75~1.X86\client\bin\runant.pl
set CONFMAN="%PROJECTROOT%\ConfigurationManagement"


SET scriptPath=%~dp0
set WASHOME_CHANGE=%scriptPath%washome_change.pl

set TARGET="%REF_DIR%\ACCESO_24\JAR_PUBLICO"



REM xcopy /y/e/r/i %PROJECTROOT%\PortalLibs  %PROJECTROOT%\%COMPONENTE%\PortalLibs\
REM copiar ConfigurationManagement
xcopy /y/e/r/i %CONFMAN% %PROJECTROOT%\%COMPONENTE%\ConfigurationManagement\
REM Ejecutar ANT
REM call ant -buildfile %PROJECTROOT%\%COMPONENTE%\%PROYECTO%\%XML%
%ant% -buildfile %PROJECTROOT%\%COMPONENTE%\%PROYECTO%\%XML%
REM Copiar EAR 
xcopy /y/e/r/i %PROJECTROOT%\%COMPONENTE%\%PROYECTO%\dist\*.jar %TARGET%\
REM Borrar directorio (es necesario?)
REM RMDIR /S /Q %PROJECTROOT%



