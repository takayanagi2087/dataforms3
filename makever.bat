@echo off
set DATAFORMS_MF=%1\src\main\java\META-INF\resources\dataforms.mf
echo %DATAFORMS_MF%
echo Manifest-Version: 1.0 > %DATAFORMS_MF%
echo Implementation-Version: %2 >> %DATAFORMS_MF%
echo Implementation-Vendor: Masahiko Takayanagi >> %DATAFORMS_MF%
echo Implementation-Title: Dataforms java web application framework >> %DATAFORMS_MF%
echo CreatedTime: %date% %time% >> %DATAFORMS_MF%
type %DATAFORMS_MF%

