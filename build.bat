@echo off
setlocal

:: --- Einstellungen ---
set PACKAGE=stan
set JARFILE=%PACKAGE%.jar
set SRC_DIR=src\%PACKAGE%
set OUT_DIR=out
set LIB_DIR=lib
set LIB_JAR=%LIB_DIR%\geo.jar

:: --- 1. Ausgabeordner leeren ---
if exist %OUT_DIR% rmdir /s /q %OUT_DIR%
mkdir %OUT_DIR%

:: --- 2. Kompilieren ---
echo [INFO] Kompiliere Java-Dateien...
javac -cp %LIB_JAR% -d %OUT_DIR% %SRC_DIR%\*.java
if errorlevel 1 (
    echo [ERROR] Kompilierung fehlgeschlagen.
    exit /b 1
)

:: --- 3. Java-Quellcode kopieren ---
echo [INFO] Kopiere .java-Dateien in das out-Verzeichnis...
xcopy /s /y %SRC_DIR% %OUT_DIR%\%PACKAGE%\ >nul

:: --- 4. JAR-Datei bauen ---
echo [INFO] Erstelle JAR-Datei...
if exist %JARFILE% del %JARFILE%
jar cf %JARFILE% -C %OUT_DIR% .

echo [SUCCESS] Fertig! -> %JARFILE%

endlocal
pause