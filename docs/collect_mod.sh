#!/bin/bash
# 获取脚本所在目录（docs）和项目根目录（docs/..）
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "Project root: $PROJECT_ROOT"
cd "$PROJECT_ROOT" || { echo "ERROR: Cannot enter project root"; exit 1; }

if [ ! -d "versions" ]; then
    echo "ERROR: 'versions' directory not found"
    exit 1
fi

mkdir -p mod-jars

for dir in versions/*/; do
    libs_dir="${dir}build/libs"
    if [ -d "$libs_dir" ]; then
        for jar in "$libs_dir"/*.jar; do
            [ -f "$jar" ] || continue
            filename=$(basename "$jar" .jar)
            # 排除以 -dev、-sources、-shadow 结尾的文件
            if [[ "$filename" != *-dev && "$filename" != *-sources && "$filename" != *-shadow ]]; then
                cp -p "$jar" mod-jars/
            fi
        done
    fi
done

ls -l mod-jars