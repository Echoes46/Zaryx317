@echo off
java -Xmx2048m
call gradle wrapper
call gradlew run
pause