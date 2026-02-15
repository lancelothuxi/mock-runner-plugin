package com.example.plugin;

import com.example.plugin.mock.MockConfig;
import com.example.plugin.service.MockConfigService;
import com.example.plugin.ui.MockRunnerToolWindowContent;
import com.google.gson.Gson;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RemoteConnection;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MockDebugRunner extends GenericDebuggerRunner {
    
    private static final Logger LOG = Logger.getInstance(MockDebugRunner.class);
    private static final String RUNNER_ID = "MyDebugProgramRunner";

    @NotNull
    @Override
    public String getRunnerId() {
        return RUNNER_ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        LOG.info("canRun called - executorId: " + executorId + 
                 ", profile: " + profile.getClass().getName() + 
                 ", MockDebugExecutor.EXECUTOR_ID: " + MockDebugExecutor.EXECUTOR_ID);
        
        boolean isMyExecutor = MockDebugExecutor.EXECUTOR_ID.equals(executorId);
        boolean isAppConfig = profile instanceof ApplicationConfiguration;
        
        LOG.info("isMyExecutor: " + isMyExecutor + ", isAppConfig: " + isAppConfig);
        
        // 只支持自定义的 Debug Executor，并且支持 Java Application 配置
        return isMyExecutor && isAppConfig;
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, 
                                             @NotNull ExecutionEnvironment environment) 
            throws ExecutionException {

        LOG.info("========== doExecute CALLED ==========");
        LOG.info("Debugging with Mock Runner!");
        LOG.info("State class: " + state.getClass().getName());

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
        LOG.info("Mock config saved to: " + configFile.getAbsolutePath());

        // 添加 JavaAgent 参数 - 通过修改 JavaCommandLineState
        if (state instanceof JavaCommandLineState) {
            JavaCommandLineState javaState = (JavaCommandLineState) state;
            try {
                JavaParameters javaParameters = javaState.getJavaParameters();
                addAgentToJavaParameters(javaParameters, configFile);
            } catch (Exception e) {
                LOG.error("Failed to add agent: " + e.getMessage(), e);
            }
        } else {
            LOG.error("State is not JavaCommandLineState: " + state.getClass().getName());
        }

        // 记录到 ToolWindow
        ApplicationManager.getApplication().invokeLater(() -> {
            MockRunnerToolWindowContent content = environment.getProject().getService(MockRunnerToolWindowContent.class);
            if (content != null) {
                content.addResult(startTime, methodName, "Debugging", "-");
            }
        });

        LOG.info("Calling super.doExecute()...");
        // 调用父类的 doExecute 来启动真正的调试会话
        return super.doExecute(state, environment);
    }
    
    // 重写 execute 方法，确保我们的代码被调用
    @Override
    public void execute(@NotNull ExecutionEnvironment environment) throws ExecutionException {
        LOG.info("========== execute() CALLED ==========");
        
        // 在执行前添加 Mock Agent
        RunProfileState state = environment.getState();
        if (state instanceof JavaCommandLineState) {
            try {
                // 从 Service 获取 Mock 配置
                MockConfigService mockConfigService = environment.getProject().getService(MockConfigService.class);
                MockConfig mockConfig = mockConfigService.getMockConfig();
                
                // 保存 Mock 配置到临时文件
                File configFile = saveMockConfig(mockConfig);
                LOG.info("Mock config saved to: " + configFile.getAbsolutePath());
                
                JavaCommandLineState javaState = (JavaCommandLineState) state;
                JavaParameters javaParameters = javaState.getJavaParameters();
                addAgentToJavaParameters(javaParameters, configFile);
            } catch (Exception e) {
                LOG.error("Failed to add agent in execute(): " + e.getMessage(), e);
            }
        }
        
        // 调用父类的 execute
        super.execute(environment);
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
        // 获取 agent jar 路径
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
            // 通过 PluginManager 获取插件路径
            com.intellij.ide.plugins.IdeaPluginDescriptor plugin = 
                com.intellij.ide.plugins.PluginManagerCore.getPlugin(
                    com.intellij.openapi.extensions.PluginId.getId("com.example.myplugin"));
            
            if (plugin != null) {
                java.nio.file.Path pluginPath = plugin.getPluginPath();
                System.out.println("[MockDebugRunner] Plugin path: " + pluginPath);
                
                // 在插件的 lib 目录下查找 agent jar
                java.nio.file.Path libPath = pluginPath.resolve("lib");
                if (java.nio.file.Files.exists(libPath)) {
                    try (java.util.stream.Stream<java.nio.file.Path> files = java.nio.file.Files.list(libPath)) {
                        java.util.Optional<java.nio.file.Path> agentJar = files
                            .filter(p -> p.getFileName().toString().startsWith("mock-agent") && 
                                       p.getFileName().toString().endsWith(".jar"))
                            .findFirst();
                        
                        if (agentJar.isPresent()) {
                            String agentPath = agentJar.get().toAbsolutePath().toString();
                            System.out.println("[MockDebugRunner] Found agent jar: " + agentPath);
                            return agentPath;
                        }
                    }
                }
            }
            
            System.err.println("[MockDebugRunner] Agent jar not found in plugin directory");
            return null;
        } catch (Exception e) {
            System.err.println("[MockDebugRunner] Error finding agent jar: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
