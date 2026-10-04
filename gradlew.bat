@echo off
setlocal
set ROOT=%~dp0
set GV=9.4.1
set CACHE=%USERPROFILE%\.gradle\localtime-wrapper\%GV%
if not exist "%CACHE%\gradle-%GV%\bin\gradle.bat" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "New-Item -ItemType Directory -Force '%CACHE%' | Out-Null; Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GV%-bin.zip' -OutFile '%CACHE%\gradle.zip'; Expand-Archive -Force '%CACHE%\gradle.zip' '%CACHE%'; Remove-Item '%CACHE%\gradle.zip'"
)
call "%CACHE%\gradle-%GV%\bin\gradle.bat" %*
