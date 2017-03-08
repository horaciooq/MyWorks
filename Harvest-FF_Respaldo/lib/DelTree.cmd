:: Script para borrar archivos y carpetas
@echo off
if "%1"=="" goto:eof
pushd %1
del /q *.* 
for /f "Tokens=*" %%G in ('dir /B') do rd /s /q "%%G"
popd 