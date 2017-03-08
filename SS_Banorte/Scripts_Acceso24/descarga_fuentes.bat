@echo off
REM Definicion de variables
REM Se van a pasar por medio del workflow, solo


set RTCUSER=HOOQH870
set RTCPASS=_Acceso26
rem set RTCCON=dataIntegracion

set lscm=call "%RTC_HOME%\scmtools\eclipse\scm.exe"
rem set scm= call "%RTC_HOME%\scmtools\eclipse\scm.exe"
REM ## set WASHOME_CHANGE=C:\rtcscripts\washome_change.pl ##Por validar
REM ## set TARGET="C:\rtcscripts\EARS" 

REM Crear directorio de trabajo
if not exist "%PROJECTROOT%\%COMPONENTE%" mkdir "%PROJECTROOT%\%COMPONENTE%"

REM Conexion a RTC
REM NOTA: el comando scm no tiene la ipcion de login
rem %lscm% login -r %RTCURL% -u %RTCUSER% -P %RTCPASS% -n %RTCCON%

REM Descargar componente a trabajar
%lscm% load -r %RTCURL% --force -u %RTCUSER% -P %RTCPASS% -d %PROJECTROOT%\%COMPONENTE% "%WORKSPACE%" "%COMPONENTE%"
REM %scm% daemon stop -a
REM descargar BttCommonRTLibs y 
%lscm% load -r %RTCURL% --force -u %RTCUSER% -P %RTCPASS% -d %PROJECTROOT%\%COMPONENTE% "%WORKSPACE%" WEB_Arquitectura_BttCommonRTLibs/BttCommonRTLibs
rem %scm% daemon stop -a
REM Revisar que no se suban a RTC las bibliotecas construidas
REM %lscm% load --force  -d %PROJECTROOT% -i "%WORKSPACE%" WEB_webCommonRTLibs/webCommonRTLibs -r %RTCCON%
%lscm% load -r %RTCURL% --force -u %RTCUSER% -P %RTCPASS% -d %PROJECTROOT%\%COMPONENTE% "%WORKSPACE%" WEB_Arquitectura_WebCommonRTLibs/webCommonRTLibs
rem %lscm% load --force  -d %PROJECTROOT% -i "%WORKSPACE%" WEB_Arquitectura_WebCommonRTLibs/webCommonRTLibs -r %RTCCON%
rem %scm% daemon stop -a

REM terminamos la sesion
rem %lscm% logout  -r %RTCCON%



