package com.example.plugin;

import com.example.plugin.mock.MockConfig;
import com.example.plugin.service.MockConfigService;
import com.example.plugin.ui.MockConfigDialog;
import com.example.plugin.ui.MockRunnerToolWindowContent;
import com.google.gson.Gson;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
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

public class MockRunner extends GenericProgramRunner {
    
    private static final Logger LOG = Logger.getInstance(MockRunner.class);
    private static final String RUNNER_ID = "MyCustomProgramRunner";

    @NotNull
    @Override
    public String getRunnerId() {
        return RUNNER_ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        LOG.info("canRun called - executorId: " + executorId + 
                 ", profile: " + profile.getClass().getName() + 
                 ", MockRunExecutor.EXECUTOR_ID: " + MockRunExecutor.EXECUTOR_ID);
        
        boolean isMyExecutor = MockRunExecutor.EXECUTOR_ID.equals(executorId);
        boolean isAppConfig = profile instanceof ApplicationConfiguration;
        
        LOG.info("isMyExecutor: " + isMyExecutor + ", isAppConfig: " + isAppConfig);
        
        // 只支持自定义的 Run Executor，并且支持 Java Application 配置
        return isMyExecutor && isAppConfig;
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, 
                                             @NotNull ExecutionEnvironment environment) 
            throws ExecutionException {
        
        LOG.info("Running with My Custom Runner!");
        
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
        
        // 添加 JavaAgent 参数 - 通过修改 CommandLineState
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
                content.addResult(startTime, methodName, "Starting", "-");
            }
        });
        
        // 执行程序并获取结果
        ExecutionResult executionResult = state.execute(environment.getExecutor(), this);
        if (executionResult == null) {
            return null;
        }
        
        // 创建并返回运行内容描述符
        return new RunContentDescriptor(
            executionResult.getExecutionConsole(),
            executionResult.getProcessHandler(),
            executionResult.getExecutionConsole().getComponent(),
            environment.getRunProfile().getName() + " (Mock)"
        );
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
            
            LOG.info("Saved mock config to: " + configFile.getAbsolutePath());
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
            LOG.info("Added agent: " + agentArg);
        } else {
            LOG.error("Agent jar not found!");
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
                LOG.info("Plugin path: " + pluginPath);
                
                // 在插件的 lib 目录下查找 agent jar
                java.nio.file.Path libPath = pluginPath.resolve("lib");
                if (java.nio.file.Files.exists(libPath)) {
                    try (java.util.stream.Stream<java.nio.file.Path> files = java.nio.file.Files.list(libPath)) {
                        java.util.Optional<java.nio.file.Path> agentJar = files
                            .filter(p -> p.getFileName().toString().startsWith("mock-agent") && 
                                       p.getFileName().toString().endsWith(".jar"))
                            .max(java.util.Comparator.comparing(p -> p.getFileName().toString()));
                        
                        if (agentJar.isPresent()) {
                            String agentPath = agentJar.get().toAbsolutePath().toString();
                            LOG.info("Found agent jar: " + agentPath);
                            return agentPath;
                        }
                    }
                }
            }
            
            LOG.error("Agent jar not found in plugin directory");
            return null;
        } catch (Exception e) {
            LOG.error("Error finding agent jar: " + e.getMessage(), e);
            return null;
        }
    }
}
