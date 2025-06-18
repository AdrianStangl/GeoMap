@echo off
REM Beispiel 1, malt die Karte des Zoos Nürnberg
java -cp geo.jar;stan.jar stan.Mapout 49.44750 11.14575 2000 1000 1500 zoo.png
echo Erzeugt zoo.png