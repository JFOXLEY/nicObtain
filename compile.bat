@ECHO OFF
CD modulecc
JAVAC -d rel src/modulecc/Program.java
cd rel
JAVA modulecc/Program ..\..\modulecc.jar modulecc jar modulecc.Program
cd ..\..

cd org.json.simple
compile.bat
jar.bat