
# 项目背景
计算机系统结构课程实验选题之算法仿真器，通过实现记分牌算法仿真器，深入理解计算机系统的底层运作，进一步学习指令调度、数据冲突、乱序执行等概念的原理和方法，并为其他人学习记分牌算法提供仿真器。

---
# 项目基本功能

记分牌仿真器的实现，根据输入作出记分牌每一时钟周期记录的三张表格：指令状态表、功能部件状态表以及寄存器结果状态表。该项目有两种输出方式：Console控制台版本以及GUI版本。
`ScoreBoard.java`实现基本功能，其中`algorithm()`方法实现了记分牌基本算法，`issue()`、`readOprand()`、`execute()`、`writeback()`分别对应记分牌的流出、读操作数、执行、写结果阶段需要执行的操作，`init2()`和 `init1()`方法分别对输出Console和GUI版本初始化，并由
`draw2()`和 `draw1()`方法实现。

---

# 使用说明
以下是两种运行方式，可根据自身需求选择：
## 1、idea运行项目
将项目加载到idea运行，在EditConfigurations中设置argument：
输出控制台版本设置为

```java
ConsolePrint
```
输出GUI版本设置为

```java
GUIPrint
```

## 2、命令行运行
JDK17及以上版本

在项目的src路径下打开命令提示符界面输入

```java
build.bat
```

`build.bat`用于编译整个项目

完成编译后，如果需要使用控制台输出，则在src路径的命令提示符界面输入

```java
run_console.bat
```
同理，如果需要使用GUI界面输出，则输入
```java
run_GUI.bat
```
---
# 项目目录结构说明
`ScoreBoard.java`用于实现项目基本功能
`FU.java`实体类，对应功能部件
`Instruction.java`实体类，对应指令
`Table`文件夹，用于控制Console版本输出表格的格式，引用自

> https://github.com/clyoudu/clyouduutil/tree/master/src/main/java/github/clyoudu/consoletable

---
# 注意事项
该记分牌仿真器修改Rj、Rk阶段在执行前期，可能与某些记分牌仿真器版本不同。
除此之外，输入的指令需要在注意操作码格式属于“LD”、“MULTD”、“SUBD”、“ADDD”、“DIVD”这几种。
