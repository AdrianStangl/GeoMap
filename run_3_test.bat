@echo off
REM Malt die Stadtmauer
java -cp geo.jar;stan.jar stan.Mapout 49.453445 11.096621 2000 1000 2000 test.png
echo Erzeugt test.png
pause