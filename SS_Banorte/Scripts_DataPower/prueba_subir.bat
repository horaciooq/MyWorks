@echo off  
set CURL=%1%  
set XML=%2%  
set XML_TMP=%3%  
set DIR=%4%  
set DOMAIN=%5%  
set LOCAL=%6%  
set USER=%7%  
set PASS=%8%  
set DP=%9%  
C:\Strawberry\perl\bin\perl.exe D:\OpenMake\DATAPOWER\Scripts\upload_data_file.pl %CURL% %XML% %XML_TMP% %DIR% %DOMAIN% %LOCAL% %USER% %PASS% %DP%  
echo done  
 
