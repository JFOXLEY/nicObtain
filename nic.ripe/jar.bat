@echo off
cd rel
mkdir META-INF
COPY ..\META-INF\CLASS.MF rel/META-INF/CLASS.MF
java -jar ..\..\modulecc.jar ..\..\..\releases\latest\nic.ripe.jar org other
cd ..