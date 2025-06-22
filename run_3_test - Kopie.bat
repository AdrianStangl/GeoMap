@echo off
REM Malt die Stadtmauer
java -cp geo.jar;stan.jar stan.Mapout 49.46579 11.15860 2000 1000 1000 joerg.png
echo Erzeugt joerg.png
pause