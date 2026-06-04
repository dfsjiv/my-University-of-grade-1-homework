@echo off
chcp 65001 >nul
echo 第一步：打包jar...
echo Main-Class: Main > manifest.txt
jar cfm EmployeeSystem.jar manifest.txt -C bin .
echo jar打包完成！
echo 第二步：打包exe，请稍等1-2分钟...
if not exist output mkdir output
jpackage --input . --main-jar EmployeeSystem.jar --main-class Main --name "员工管理系统" --app-version 1.0 --type app-image --dest output --java-options "-Dfile.encoding=UTF-8" --java-options "-Dstdout.encoding=UTF-8"
echo 完成！app-image文件夹在output文件夹里，可直接运行
pause
