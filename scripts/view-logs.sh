#!/bin/bash
echo "=== 查看 Mock Agent 日志 ==="
echo ""
echo "1. /tmp/mock-agent.log:"
if [ -f /tmp/mock-agent.log ]; then
    tail -100 /tmp/mock-agent.log
else
    echo "   文件不存在"
fi

echo ""
echo "2. Mock 配置文件:"
if [ -f /tmp/mock-runner/mock-config.json ]; then
    cat /tmp/mock-runner/mock-config.json
else
    echo "   文件不存在"
fi

echo ""
echo "3. 最近的 idea.log (最后 50 行):"
if [ -f build/idea-sandbox/system/log/idea.log ]; then
    tail -50 build/idea-sandbox/system/log/idea.log
else
    echo "   文件不存在"
fi
