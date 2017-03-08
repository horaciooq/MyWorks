@echo off

REM declarar variables
set VERSION=%2
set ESTADO=%1

REM iniciar sesion
D:\OpenMake\DATAPOWER\curl\bin\curl.exe --data "user=admin&pass=admin" --cookie-jar D:\OpenMake\DATAPOWER\session.txt http://15.128.18.84:8081/dmadminweb/API/login

REM correr deploy en RE
D:\OpenMake\DATAPOWER\curl\bin\curl.exe --data "wait=Y&env=Desarrollo1" --cookie D:\OpenMake\DATAPOWER\session.txt http://15.128.18.84:8081/dmadminweb/API/deploy/GLOBAL.DataPower.%ESTADO%%%3B%VERSION%