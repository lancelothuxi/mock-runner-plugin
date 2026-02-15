package com.example.plugin;

import com.example.plugin.mock.MockConfig;
import com.example.plugin.service.MockConfigService;
import com.example.plugin.ui.MockConfigDialog;
import com.example.plugin.ui.MyRunnerToolWindowContent;
import com.google.gson.Gson;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyProgramRunner extends GenericProgramRunner {
    
    private static final String RUNNER_ID = "MyCustomProgramRunner";

    @NotNull
    @Override
    public String getRunnerId() {
        return RUNNER_ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        // 只支持我们自定义的 Executor
        return MyCustomExecutor.EXECUTOR_ID.equals(executorId);
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, 
                                             @NotNull ExecutionEnvironment environment) 
            throws ExecutionException {
        
        System.out.println("Running with My Custom Runner!");
        
        // 显示 ToolWindow
        ApplicationManager.getApplication().invokeLater(() -> {
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(environment.getProject());
            ToolWindow toolWindow = toolWindowManager.getToolWindow("My Runner");
            if (toolWindow != null) {
                toolWindow.show();
            }
        });
        
        // 记录开始时间
        String startTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String methodName = environment.getRunProfile().getName();
        
        // 从 Service 获取 Mock 配置
        MockConfigService mockConfigService = environment.getProject().getService(MockConfigService.class);
        MockConfig mockConfig = mockConfigService.getMockConfig();
        
        // 保存 Mock 配置到临时文件
        File configFile = saveMockConfig(mockConfig);
        
        // 添加 JavaAgent 参数
        if (state instanceof JavaParameters) {
            addAgentToJavaParameters((JavaParameters) state, configFile);
        }
        
        // 记录到 ToolWindow
        ApplicationManager.getApplication().invokeLater(() -> {
            MyRunnerToolWindowContent content = environment.getProject().getService(MyRunnerToolWindowContent.class);
            if (content != null) {
                content.addResult(startTime, methodName, "Starting", "-");
            }
        });
        
        // 执行程序并获取结果
        ExecutionResult executionResult = state.execute(environment.getExecutor(), this);
        if (executionResult == null) {
            return null;
        }
        
        // 创建运行内容描述符
        RunContentDescriptor descriptor = new RunContentDescriptor(
            executionResult.getExecutionConsole(),
            executionResult.getProcessHandler(),
            executionResult.getExecutionConsole().getComponent(),
            environment.getRunProfile().getName() + " (Mock)"
        );
        
        // 显示运行内容
        RunContentManager.getInstance(environment.getProject())
                .showRunContent(environment.getExecutor(), descriptor);
        
        return descriptor;
    }
    
    private File saveMockConfig(MockConfig mockConfig) throws ExecutionException {
        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "mock-runner");
            tempDir.mkdirs();
            
            File configFile = new File(tempDir, "mock-config.json");
            
            Gson gson = new Gson();
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(mockConfig, writer);
            }
            
            System.out.println("[MockRunner] Saved mock config to: " + configFile.getAbsolutePath());
            return configFile;
        } catch (Exception e) {
            throw new ExecutionException("Failed to save mock config", e);
        }
    }
    
    private void addAgentToJavaParameters(JavaParameters javaParameters, File configFile) {
        // 获取 agent jar 路径（需要构建 agent jar）
        String agentJarPath = getAgentJarPath();
        
        if (agentJarPath != null) {
            String agentArg = "-javaagent:" + agentJarPath + "=" + configFile.getAbsolutePath();
            javaParameters.getVMParametersList().add(agentArg);
            System.out.println("[MockRunner] Added agent: " + agentArg);
        } else {
            System.err.println("[MockRunner] Agent jar not found!");
        }
    }
    
    private String getAgentJarPath() {
        try {
            // 从插件目录获取 agent jar
            String pluginPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            File pluginDir = new File(pluginPath).getParentFile();
            
            // 查找 agent jar
            File[] agentJars = pluginDir.listFiles((dir, name) -> 
                name.startsWith("mock-agent") && name.endsWith(".jar"));
            
            if (agentJars != null && agentJars.length > 0) {
                String agentPath = agentJars[0].getAbsolutePath();
                System.out.println("[MockRunner] Found agent jar: " + agentPath);
                return agentPath;
            }
            
            System.err.println("[MockRunner] Agent jar not found in: " + pluginDir.getAbsolutePath());
            return null;
        } catch (Exception e) {
            System.err.println("[MockRunner] Error finding agent jar: " + e.getMessage());
            return null;
        }
    }
}
