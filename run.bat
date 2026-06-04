@echo off
chcp 65001 >nul
echo 正在编译...
javac -encoding UTF-8 -d bin src\model\*.java src\service\*.java src\ui\*.java src\ui\modern\*.java src\Main.java
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b %errorlevel%
)
echo 编译成功！启动程序...
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp bin Main
pause
