@echo off

REM establecer variables
SET ESTADO=%1
IF %ESTADO%==Desarrollo goto :desarrollo
IF %ESTADO%==PreProduccion goto :preproduccion
IF %ESTADO%==Produccion goto :produccion
IF %ESTADO%==Integracion goto :integracion

:ejecutar
REM iniciar sesion
D:\OpenMake\DATAPOWER\curl\bin\curl.exe --data "user=admin&pass=admin" --cookie-jar D:\OpenMake\DATAPOWER\session.txt http://15.128.18.84:8081/dmadminweb/API/login

REM crear version
D:\OpenMake\DATAPOWER\curl\bin\curl.exe --cookie D:\OpenMake\DATAPOWER\session.txt http://15.128.18.84:8081/dmadminweb/API/newappver/GLOBAL.DataPower.%ESTADO%%%3B%CURRENT%?taskname=GLOBAL.DataPower.CrearVersion

REM cambiar valor de version
D:\OpenMake\DATAPOWER\curl\bin\curl.exe --cookie D:\OpenMake\DATAPOWER\session.txt http://15.128.18.84:8081/dmadminweb/API/setvar/application/%ESTADO%%%3B%VER%?name=version^&value=%VER%

REM ejecutar despliegue
D:\OpenMake\DATAPOWER\curl\bin\curl.exe --data "wait=Y&env=Desarrollo1" --cookie D:\OpenMake\DATAPOWER\session.txt http://15.128.18.84:8081/dmadminweb/API/deploy/GLOBAL.DataPower.%ESTADO%%%3B%VER%
goto :eof

REM Seteamos los valores de las variables para PreProduccion
:preproduccion
SET /a VER=%DP_PRE_VERSION%+1
SET CURRENT=%DP_PRE_VERSION%
SETX DP_PRE_VERSION %VER%
goto :ejecutar

REM Seteamos los valores de las variables para Produccion
:produccion
SET /a VER=%DP_PRO_VERSION%+1
SET CURRENT=%DP_PRO_VERSION%
SETX DP_PRO_VERSION %VER%
goto :ejecutar

REM Seteamos los valores de las variables para Desarrollo
:desarrollo
SET /a VER=%DP_VERSION%+1
SET CURRENT=%DP_VERSION%
SETX DP_VERSION %VER%
goto :ejecutar

REM Seteamos los valores de las variables para Integracion
:integracion
SET /a VER=%DP_INT_VERSION%+1
SET CURRENT=%DP_INT_VERSION%
SETX DP_INT_VERSION %VER%
goto :ejecutar