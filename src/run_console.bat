:start
cls
cd /d %~dp0

java -classpath "%~dp0\";. ScoreBoard ConsolePrint

echo finish!
pause