@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  DDA_Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and DDA_GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\DDA_Gradle-1.0.jar;%APP_HOME%\lib\commons-collections-3.2.1.jar;%APP_HOME%\lib\commons-configuration-1.6.jar;%APP_HOME%\lib\commons-io-1.4.jar;%APP_HOME%\lib\commons-lang-2.5.jar;%APP_HOME%\lib\commons-logging-1.1.1.jar;%APP_HOME%\lib\org.eclipse.core.contenttype_3.4.1.R35x_v20090826-0451.jar;%APP_HOME%\lib\org.eclipse.core.jobs_3.4.100.v20090429-1800.jar;%APP_HOME%\lib\org.eclipse.core.resources-3.8.100.v20130521-2026.jar;%APP_HOME%\lib\org.eclipse.core.resources_3.5.2.R35x_v20091203-1235.jar;%APP_HOME%\lib\org.eclipse.core.runtime-3.7.0.jar;%APP_HOME%\lib\org.eclipse.core.runtime_3.5.0.v20090525.jar;%APP_HOME%\lib\org.eclipse.equinox.common-3.6.0.v20100503.jar;%APP_HOME%\lib\org.eclipse.equinox.common_3.5.1.R35x_v20090807-1100.jar;%APP_HOME%\lib\org.eclipse.equinox.preferences_3.2.301.R35x_v20091117.jar;%APP_HOME%\lib\org.eclipse.jdt.astview_1.1.8.201108081127 (1) - Copy.jar;%APP_HOME%\lib\org.eclipse.jdt.astview_1.1.8.201108081127 (1).jar;%APP_HOME%\lib\org.eclipse.jdt.astview_1.1.8.201108081127.jar;%APP_HOME%\lib\org.eclipse.jdt.astview_1.1.9.201406161921.jar;%APP_HOME%\lib\org.eclipse.jdt.core-3.8.0.v_c18.jar;%APP_HOME%\lib\org.eclipse.osgi-3.7.1.jar;%APP_HOME%\lib\org.eclipse.osgi_3.5.2.R35x_v20100126.jar;%APP_HOME%\lib\org.eclipse.text_3.5.0.jar;%APP_HOME%\lib\tools.jar;%APP_HOME%\lib\slf4j-api-1.7.21.jar

@rem Execute DDA_Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %DDA_GRADLE_OPTS%  -classpath "%CLASSPATH%" Setup %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable DDA_GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%DDA_GRADLE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
