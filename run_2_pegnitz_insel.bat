@echo off
REM Erzeugt diese kleine "Insel" in der Pegnitz nahe des Woerder Sees
java -cp geo.jar;stan.jar stan.Mapout 49.452831 11.082716 1000 500 1200 pegnitzInsel.png
echo Erzeugt pegnitzInsel.png