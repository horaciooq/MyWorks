@echo off
SET mypath=%~dp0

for /f %%i in ('perl %mypath%calculaVersion.pl') do set VER=%%i

"C:\Program Files\Java\jre7\bin\java.exe"  -Dconfig.properties=%mypath%config.properties -jar %mypath%re.jar  %* -version %VER%

