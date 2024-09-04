@ECHO OFF
echo Building modulecc
echo ======
CD modulecc
JAVAC -d rel src/modulecc/Program.java
cd rel
echo Running modulecc for self
echo ======
JAVA modulecc/Program ..\..\modulecc.jar modulecc jar modulecc.Program
cd ..\..

echo ======
echo Compiling JSON dependency
echo ======
cd org.json.simple
call compile.bat
call jar.bat
cd ..

echo ======
echo Compiling FTP dependency
echo ======
cd ftp
call compile.bat
call jar.bat
cd ..

echo ======
echo Compiling nicObtain [api v2]
echo ======
cd nic
call compile.bat
call jar.bat
cd ..

pause