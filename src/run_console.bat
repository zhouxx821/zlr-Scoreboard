:start
cls
cd /d %~dp0

java -classpath %JAVA_HOME%\jre\lib\deploy.jar;%~dp0 ScoreBoard ConsolePrint

echo finish!
pause