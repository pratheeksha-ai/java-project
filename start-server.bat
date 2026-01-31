@echo off
cd /d "%~dp0\src"
javac -cp ".;../lib/mysql-connector-j-9.6.0.jar" *.java
java -cp ".;../lib/mysql-connector-j-9.6.0.jar" WebServer
pause
