@echo off
REM Definicion de variables
REM Se van a pasar por medio del workflow, solo

set PROJECTROOT=%1%
set RTCUSER=%2%
set RTCPASS=%3%
set RTCCON=%4%
set RTCURL=%5%
set WS=%6%
set COMPONENTE=%7%
set RTC_HOME=%8%


set lscm=call %RTC_HOME%\scmtools\eclipse\lscm.bat
set scm= call %RTC_HOME%\scmtools\eclipse\scm.exe
REM ## set WASHOME_CHANGE=C:\rtcscripts\washome_change.pl ##Por validar
REM ## set TARGET="C:\rtcscripts\EARS" 

REM Crear directorio de trabajo
if not exist "%PROJECTROOT%" goto :DESCARGA
goto:eof 

:DESCARGA
REM crear directorio
mkdir "%PROJECTROOT%"
REM Conexion a RTC
%scm% login -r %RTCURL% -u %RTCUSER% -P %RTCPASS% -n %RTCCON%

REM Descargar componente a trabajar
%scm% load --force  -d %PROJECTROOT% -i "%WS%" "%COMPONENTE%" -r %RTCCON%


REM terminamos la sesion
%scm% logout  -r %RTCCON%



