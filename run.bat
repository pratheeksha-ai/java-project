@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0src"
echo Compiling Java files...
javac -cp ".;../lib/mysql-connector-j-9.6.0.jar" *.java
if %errorlevel% neq 0 (
  echo Compilation failed!
  pause
  exit /b 1
)
echo Starting Library Management System...
echo Opening browser to http://localhost:8080
timeout /t 2
start http://localhost:8080
java -cp ".;../lib/mysql-connector-j-9.6.0.jar" WebServer
pause
