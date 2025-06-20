@echo off
REM Malt die Stadtmauer
java -cp geo.jar;stan.jar stan.Mapout 49.44698 11.07413 2000 1000 1500 Stadtmauer.png
echo Erzeugt Stadtmauer.png
pause