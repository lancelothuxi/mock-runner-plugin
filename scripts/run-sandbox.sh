#!/bin/bash
# 启动 sandbox - 后台运行

echo "启动 IntelliJ sandbox..."
echo "日志输出到: runide.log"
echo ""

nohup ./gradlew runIde > runide.log 2>&1 &
GRADLE_PID=$!

echo "Gradle PID: $GRADLE_PID"
echo ""
echo "查看日志: tail -f runide.log"
echo "停止 sandbox: pkill -f idea-sandbox"
echo ""
echo "等待 sandbox 启动（约 1-2 分钟）..."
