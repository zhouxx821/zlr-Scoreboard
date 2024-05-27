:start
cls
cd /d %~dp0Table

javac  Align.java
javac -classpath %~dp0 Cell.java
javac -classpath %~dp0 Body.java
javac -classpath %JAVA_HOME%\jre\lib\deploy.jar;%~dp0  ConsoleTable.java
javac -classpath %~dp0  Header.java
javac -classpath %~dp0  NullPolicy.java
javac -classpath %~dp0  PrintUtil.java
javac -classpath %~dp0 StringPadUtil.java

cd /d %~dp0
javac FU.java
javac -classpath %~dp0  Instruction.java
javac -classpath %JAVA_HOME%\jre\lib\deploy.jar;%~dp0  ScoreBoard.java

echo finish!
pause

