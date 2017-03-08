@echo off
REM Definicion de variables estaticas
set RTC_HOME=D:\Program Files\IBM\TeamConcert
set RTCUSER=HOOQH870
set RTCPASS=DZLN92RJ
set RTCCON=dataFortify
set lscm=call "%RTC_HOME%\scmtools\eclipse\lscm.bat"
set scm= call "%RTC_HOME%\scmtools\eclipse\scm.exe"

REM DEfinicion de variables variables
set COMPONENTE=%1%
set WS=%2%
set RTCURL=%3%
set PROJECTROOT=%4%

REM Crear directorio de trabajo
if not exist "%PROJECTROOT%" mkdir "%PROJECTROOT%"

REM Conexion a RTC
%scm% login -r %RTCURL% -u %RTCUSER% -P %RTCPASS% -n %RTCCON%

REM Descargar componente a trabajar
%scm% load --force  -d %PROJECTROOT% -i "%WS%" "%COMPONENTE%" -r %RTCCON%

REM descargar BttCommonRTLibs y 
%scm% load --force  -d %PROJECTROOT% -i "%WS%" WEB_Arquitectura_BttCommonRTLibs/BttCommonRTLibs -r %RTCCON%

REM Revisar que no se suban a RTC las bibliotecas construidas
%scm% load --force  -d %PROJECTROOT% -i "%WS%" WEB_webCommonRTLibs/webCommonRTLibs -r %RTCCON%

REM terminamos la sesion
%scm% logout  -r %RTCCON%



