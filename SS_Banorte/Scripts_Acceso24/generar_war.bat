@echo on


set ant=call perl "%OM_HOME%\client\bin\runant.pl"
REM set CONFMAN="%PROJECTROOT%\ConfigurationManagement"
set CONFMAN="%REF_DIR%\ACCESO_24\ConfigurationManagement"

SET scriptPath=%~dp0
set WASHOME_CHANGE=%scriptPath%washome_change.pl
set PORTAL_CHANGE=%scriptPath%wasportal_change.pl

set TARGET="%REF_DIR%\ACCESO_24\WARS_PUBLICO\%ESTADO%"

REM Copiar librerias
xcopy /y/e/r/i %PROJECTROOT%\PortalLibs  %PROJECTROOT%\%COMPONENTE%\PortalLibs\
REM copiar ConfigurationManagement
xcopy /y/e/r/i %CONFMAN% %PROJECTROOT%\%COMPONENTE%\ConfigurationManagement\
REM Cambiar propiedades
perl %WASHOME_CHANGE% %PROJECTROOT%\%COMPONENTE%\%PROYECTO%\%PROPS%.properties
perl %PORTAL_CHANGE% %PROJECTROOT%\%COMPONENTE%\%PROYECTO%\%PROPS%.properties
REM Ejecutar ANT
%ant% -buildfile  %PROJECTROOT%\%COMPONENTE%\%PROYECTO%\%XML%
REM call ant -buildfile %PROJECTROOT%\%COMPONENTE%\%PROYECTO%\%XML%
REM Copiar EAR 
if not exist "%TARGET%" mkdir "%TARGET%"
xcopy /y/e/r/i %PROJECTROOT%\%COMPONENTE%\%PROYECTO%\dist\*.war %TARGET%\
REM Se se agrega la variable DEBUG=1 el directorio no se borra
REM IF "%DEBUG%"=="" (RMDIR /S /Q %PROJECTROOT%\%COMPONENTE%)



