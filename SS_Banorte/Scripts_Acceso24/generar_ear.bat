@echo off
set ant=call perl "%OM_HOME%\client\bin\runant.pl"
set CONFMAN="%REF_DIR%\ACCESO_24\ConfigurationManagement"
SET scriptPath=%~dp0
set WASHOME_CHANGE=%scriptPath%washome_change.pl
set TARGET="%REF_DIR%\ACCESO_24\EARS\%ESTADO%\%PROYECTO%\%VER%"
REM xcopy /y/e/r/i %PROJECTROOT%\BttCommonRTLibs %PROJECTROOT%\%COMPONENTE%\BttCommonRTLibs\
REM xcopy /y/e/r/i %PROJECTROOT%\webCommonRTLibs %PROJECTROOT%\%COMPONENTE%\webCommonRTLibs\
REM copiar ConfigurationManagement
xcopy /y/e/r/i %CONFMAN% %PROJECTROOT%\%COMPONENTE%\ConfigurationManagement\
REM cambio en archivos properties del WAS_8_HOME ya que no se instalo en el directorio default
rem set PROYECTO=%COMPONENTE:~17%
perl %WASHOME_CHANGE% %PROJECTROOT%\%COMPONENTE%\%PROYECTO%\%PROPS%.properties
perl %WASHOME_CHANGE% %PROJECTROOT%\%COMPONENTE%\%PROYECTOEAR%\%PROPS%_ear.properties
REM Ejecutar ANT
%ant% -buildfile %PROJECTROOT%\%COMPONENTE%\%PROYECTOEAR%\%PROYECTOEARXML%
rem if %ERRORLEVEL% neq 0 exit /b %errorlevel%
echo %ERRORLEVEL%
REM call ant -buildfile %PROJECTROOT%\%COMPONENTE%\%PROYECTOEAR%\%PROYECTOEARXML%
REM Copiar EAR 
if exist %PROJECTROOT%\%COMPONENTE%\%PROYECTOEAR%\deploy\*.ear (
  if not exist %TARGET% mkdir %TARGET%
  xcopy /y/e/r/i %PROJECTROOT%\%COMPONENTE%\%PROYECTOEAR%\deploy\*.ear %TARGET%\
)
REM Borrar directorio (es necesario?)
REM Se se agrega la variable DEBUG=1 el directorio no se borra
IF "%DEBUG%"=="" (RMDIR /S /Q %PROJECTROOT%\%COMPONENTE%)
