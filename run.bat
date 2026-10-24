@echo off
cd /d "%~dp0"
echo Compiling Student Grade Calculator...
javac StudentGradeServer.java
if errorlevel 1 (
  echo Compilation failed. Make sure Java JDK 11 or newer is installed and javac is on PATH.
  pause
  exit /b 1
)
echo Starting server at http://localhost:8765
java StudentGradeServer
pause
