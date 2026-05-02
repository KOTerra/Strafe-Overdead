@echo off
set "JAVA_21_HOME=C:\Users\miche\.jdks\openjdk-21.0.2"
set "JAVA_17_HOME=C:\Users\miche\.jdks\corretto-17.0.10"
set "ARTICY_DIR=D:\Games\CV9 projekt red\Articy-Java-Runtime"
set "STRAFE_DIR=D:\Games\CV9 projekt red\Strafe Overdead\StrafeOverdead\StrafeOverdead"

echo [1/3] Publishing Articy Java Runtime to Maven Local...
set "JAVA_HOME=%JAVA_17_HOME%"
pushd "%ARTICY_DIR%"
call "%STRAFE_DIR%\gradlew.bat" publishToMavenLocal
if %ERRORLEVEL% NEQ 0 (
    echo Error publishing Articy Runtime.
    popd
    pause
    exit /b %ERRORLEVEL%
)
popd

echo.
echo [2/3] Building Strafe Overdead (Core and Desktop)...
set "JAVA_HOME=%JAVA_21_HOME%"
pushd "%STRAFE_DIR%"
call .\gradlew.bat :core:build :desktop:build
if %ERRORLEVEL% NEQ 0 (
    echo Error building Strafe Overdead.
    popd
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [3/3] Updating IntelliJ IDEA configuration...
call .\gradlew.bat idea
if %ERRORLEVEL% NEQ 0 (
    echo Error updating IDEA config.
)

popd
echo.
echo Done! Please reload the Gradle project in IntelliJ.
pause
