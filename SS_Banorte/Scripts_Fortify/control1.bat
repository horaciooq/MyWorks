@echo off
REM script de escaneo Acceso 24

REM Declaracion de variables
set REF_DIR=%1%
set PROYECTO=%2%
set COMPONENTE=%3%
set WS=%4%
set RTCURL=%5%
set MEMORY=%6%
set FOLIO=%7%
set PROYECTO_SSC=%8%
set 
set PROJECTROOT=%REF_DIR%\run\%PROYECTO%_int
set TARGET_DIR=\\15.128.27.58\SourceCode\FortifyRTC\%PROYECTO%_int

if not exist %PROJECTROOT% mkdir %PROJECTROOT%
if not exist %TARGET_DIR% mkdir %TARGET_DIR%
REM Descarga de codigo fuente este se unira con el descargar fuentes previo de openmake
call %REF_DIR%\Scripts\descarga_fuentes.bat %COMPONENTE% %WS% %RTCURL% %PROJECTROOT%
REM Copiado de codigo fuente
xcopy /y/e/r/i %PROJECTROOT% %TARGET_DIR%
REM Ejecucion de escaneo remoto
REM perl "C:\Harvest-FF\RTC\Scripts\scan.pl" %TARGET_DIR%\%COMPONENTE%\%PROYECTO%
hexecp -prg perl -ma -args "C:\Harvest-FF\RTC\Scripts\scan_DP.pl" %TARGET_DIR%\%COMPONENTE%\%PROYECTO%  %MEMORY% %FOLIO% %PROYECTO_SSC% -syn -m 15.128.19.108 -er D:\Fortify\Scripts\agente.dfo -rport 1576

if not exist %REF_DIR%\Resultados\%PROYECTO% mkdir %REF_DIR%\Resultados\%PROYECTO%
xcopy /y/r/i %TARGET_DIR%\%COMPONENTE%\%PROYECTO%\results.txt %REF_DIR%\Resultados\%PROYECTO%\

FOR /F "delims=·" %%A IN (%REF_DIR%\Resultados\%PROYECTO%\results.txt) DO set resultado=%%A

set /a i=0
call :a00 %resultado%
set VUL=%array_1_%
set FP=%array_2_%
set C=%array_3_%
set A=%array_4_%

echo vulnerabilidades=%VUL%
echo Falsos positivos=%FP%
echo Criticas=%C%
echo Altas=%A%

REM Se se agrega la variable DEBUG=1 el directorio no se borra
IF "%DEBUG%"=="" (RMDIR /S /Q %PROJECTROOT%)

goto :EOF
REM --
:a00
if {%1}=={} goto :EOF
set /a i=i+1
set array_%i%_=%1
shift
goto :a00

