# Contributing Guide

[English] | [[简体中文]](Contributing_zh_cn.md)

## Build

Build all mc versions mods:  
`./gradlew build`  
If you want to build single mc version mod:  
`./gradlew {version}:build`  
Such as: `./gradlew 1.20.6:build`  
The built jar file is located in the 'version\{version}\build\libs' directory  

Collect all built jar files to the 'mod-jars' directory under the project root directory:  
Windows: `cd docs` `.\collect_build.bat`  
Linux/Mac: `cd docs` `./collect_build.sh`  
Or run `./gradlew buildAndGather` to collect the JAR files for all versions built into `build\libs`.

## Pull Request
Feel free to submit a Pull Request for this mod!   
Submit the pull request to the `dev` branch!  
The main branch `mojang/preprocessor` will be merged from the `dev` branch and released after each **Minecraft update** or **bug fix**.