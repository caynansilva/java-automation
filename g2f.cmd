@echo off
setlocal
pushd "%~dp0"
set "G2F_ARGS=%*"
call mvn -q -DskipTests compile exec:java "-Dexec.args=%G2F_ARGS%"
set "G2F_EXIT=%ERRORLEVEL%"
popd
exit /b %G2F_EXIT%
