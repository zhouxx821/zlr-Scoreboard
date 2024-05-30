:start
cls
cd /d %~dp0

java -classpath "%~dp0\";. ScoreBoard GUIPrint

echo finish!
pause