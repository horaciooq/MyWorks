@echo on
REM Definicion de variables
REM Se van a pasar por medio del workflow, solo


set RTCUSER=HOOQH870
set RTCPASS=_Acceso26
set RTCCON=dataIntegracion

set lscm=call "%RTC_HOME%\scmtools\eclipse\lscm.bat"
set scm= call "%RTC_HOME%\scmtools\eclipse\scm.exe"
REM ## set WASHOME_CHANGE=C:\rtcscripts\washome_change.pl ##Por validar
REM ## set TARGET="C:\rtcscripts\EARS" 

REM Crear directorio de trabajo
if not exist "%PROJECTROOT%" mkdir "%PROJECTROOT%"

REM Conexion a RTC
%scm% login -r %RTCURL% -u %RTCUSER% -P %RTCPASS% -n %RTCCON%

REM Descargar componente a trabajar
%scm% load --force  -d %PROJECTROOT% -i "%WS%" "%COMPONENTE%" -r %RTCCON%
REM descargar BttCommonRTLibs y 
%scm% load --force  -d %PROJECTROOT% -i "%WS%" WEB_PPublico_PortalLibs/PortalLibs -r %RTCCON%
REM Revisar que no se suban a RTC las bibliotecas construidas
REM %scm% load --force  -d %PROJECTROOT% -i "%WS%" Configuration_Management/ConfigurationManagement -r %RTCCON%
REM %scm% daemon stop -a
REM terminamos la sesion
%scm% logout  -r %RTCCON%



