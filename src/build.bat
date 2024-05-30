:start
cls
cd /d %~dp0Table

javac  Align.java
javac -encoding UTF-8 -classpath "%~dp0\";. Cell.java
javac -encoding UTF-8 -classpath "%~dp0\";. Body.java
javac -encoding UTF-8 -classpath "%~dp0\";.  ConsoleTable.java
javac -encoding UTF-8 -classpath "%~dp0\";.  Header.java
javac -encoding UTF-8 -classpath "%~dp0\";.  NullPolicy.java
javac -encoding UTF-8 -classpath "%~dp0\";.  PrintUtil.java
javac -encoding UTF-8 -classpath "%~dp0\";. StringPadUtil.java

cd /d %~dp0
javac -encoding UTF-8 -classpath "%~dp0\";. FU.java
javac -encoding UTF-8 -classpath "%~dp0\";.  Instruction.java
javac -encoding UTF-8 -classpath "%~dp0\";.  ScoreBoard.java

echo finish!
pause

