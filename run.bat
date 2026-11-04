@echo off
setlocal enabledelayedexpansion
set SRC=src\main\java
set OUT=out

if not exist %OUT% mkdir %OUT%

if exist sources.txt del sources.txt
for /r "%SRC%" %%f in (*.java) do (
    set "p=%%f"
    set "p=!p:\=/!"
    echo "!p!">>sources.txt
)

javac -d %OUT% @sources.txt
set BUILD_ERR=%ERRORLEVEL%
del sources.txt
if %BUILD_ERR% neq 0 (
    echo Error de compilacion.
    exit /b 1
)

java -cp %OUT% bridge.Main
