@echo on
set XMLACCESS=%1
set URLCONFIG=%2
set WPUSER=%3
set WPPASS=%4
set WEBAPP=%~5
set PORTLETAPP=%~6
set WAR=%~7

set SCRIPT_DIR=%TEMP%\
rem echo %*
rem echo %SCRIPT_DIR%
rem echo  "%SCRIPT_DIR%%PORTLETAPP%.xml%"

echo ^<?xml version="1.0" encoding="UTF-8"?^> > "%SCRIPT_DIR%%PORTLETAPP%.xml%"
echo ^<request xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" >> "%SCRIPT_DIR%%PORTLETAPP%.xml%"
echo    xsi:noNamespaceSchemaLocation="PortalConfig_8.0.0.xsd" >> "%SCRIPT_DIR%%PORTLETAPP%.xml%" 
echo    type="update" create-oids="true"^> >> "%SCRIPT_DIR%%PORTLETAPP%.xml%" 
echo    ^<portal action="locate"^> >> "%SCRIPT_DIR%%PORTLETAPP%.xml%" 
echo      ^<web-app action="update" active="true" uid="%WEBAPP%"^> >> "%SCRIPT_DIR%%PORTLETAPP%.xml%" 
echo          ^<url^>file:%WAR%^</url^> >> "%SCRIPT_DIR%%PORTLETAPP%.xml%" 
echo          ^<!-- The uid must match uid attribute of concrete-portlet-app in portlet.xml. --^> >> "%SCRIPT_DIR%%PORTLETAPP%.xml%" 
echo          ^<!-- The name attribute must match content of portlet-name subtag  of concrete-portlet in portlet.xml. --^> >> "%SCRIPT_DIR%%PORTLETAPP%.xml%" 
echo          ^<portlet-app action="update" active="true" uid="%PORTLETAPP%"^> >> "%SCRIPT_DIR%%PORTLETAPP%.xml%" 
echo          ^</portlet-app^> >> "%SCRIPT_DIR%%PORTLETAPP%.xml%" 
echo        ^</web-app^> >> "%SCRIPT_DIR%%PORTLETAPP%.xml%" 
echo     ^</portal^> >> "%SCRIPT_DIR%%PORTLETAPP%.xml%" 
echo ^</request^> >> "%SCRIPT_DIR%%PORTLETAPP%.xml%"

rem more "%SCRIPT_DIR%%PORTLETAPP%.xml%"

rem echo %XMLACCESS% -url %URLCONFIG% -in "%SCRIPT_DIR%%PORTLETAPP%.xml%"    -user    %WPUSER%   -password   %WPPASS%
%XMLACCESS% -url %URLCONFIG% -in "%SCRIPT_DIR%%PORTLETAPP%.xml%" -user admin -password admin