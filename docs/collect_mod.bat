@echo off
setlocal enabledelayedexpansion

REM 获取脚本所在目录（docs）和项目根目录（docs\..）
set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%.."
for %%i in ("%PROJECT_ROOT%") do set "PROJECT_ROOT=%%~fi"

echo Project root: %PROJECT_ROOT%
cd /d "%PROJECT_ROOT%" || (
    echo ERROR: Cannot enter project root
    exit /b 1
)

if not exist versions (
    echo ERROR: 'versions' directory not found
    exit /b 1
)

if not exist mod-jars mkdir mod-jars

for /d %%D in (versions\*) do (
    set "libs_dir=%%D\build\libs"
    if exist "!libs_dir!\" (
        for %%F in ("!libs_dir!\*.jar") do (
            set "filename=%%~nF"
            REM 检查文件名是否以 -dev, -sources, -shadow 结尾
            set "exclude="
            if "!filename:~-4!"=="-dev" set exclude=1
            if "!filename:~-8!"=="-sources" set exclude=1
            if "!filename:~-7!"=="-shadow" set exclude=1
            if not defined exclude (
                copy "%%F" "mod-jars\" >nul
            )
        )
    )
)

dir mod-jars
endlocal