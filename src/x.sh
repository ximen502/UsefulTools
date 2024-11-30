#!/bin/bash

# 检查是否提供了参数
if [ $# -ne 1 ]; then
    echo "请提供一个Java源文件作为参数"
    exit 1
fi

java_file=$1

# 检查文件是否存在且是Java源文件
# if [! -f $java_file ] ||! [[ $java_file == *.java ]]; then
#     echo "提供的不是有效的Java源文件"
#     exit 1
# fi

# 获取文件名（不带扩展名）
class=$(echo $java_file | awk -F '.' '{print $1}')
# echo $class

# 编译Java源文件
javac -encoding utf-8 $java_file

# 检查编译是否成功
if [ $? -ne 0 ]; then
    echo "编译失败"
    exit 1
else
    echo "==编译成功，准备运行=="
fi

# 运行编译后的类
java $class

if [ $? -ne 0 ]; then
    echo "运行失败"
    exit 1
else
    echo "运行完毕"
fi