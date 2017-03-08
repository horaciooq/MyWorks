@echo off

REM Load Visual Studio's build tools
call "%ProgramFiles(x86)%\Microsoft Visual Studio 10.0\VC\vcvarsall.bat" x86

REM Choose what you want to do, 1 or 2 by (un)commenting

REM     1. Add your cl.exe (or msbuild.exe or other) commands here
REM msbuild.exe MyProject.csproj
devenv %*
REM custom-step.exe  %*
REM pause

REM     2. Open a normal interactive system command shell with all variables loaded
REM %comspec% /k