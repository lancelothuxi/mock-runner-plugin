package io.github.lancelothuxi.idea.plugin.mock.service;

import io.github.lancelothuxi.idea.plugin.mock.mock.MockConfig;
import io.github.lancelothuxi.idea.plugin.mock.mock.MockMethodConfig;
import io.github.lancelothuxi.idea.plugin.mock.ui.MockRunnerToolWindowContent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@State(
    name = "MockConfigService",
    storages = @Storage("mockRunnerConfig.xml")
)
public class MockConfigService implements PersistentStateComponent<MockConfigService.State> {
    private static final Logger LOG = Logger.getInstance(MockConfigService.class);
    private final Project project;
    private MockConfig mockConfig;
    
    public MockConfigService(Project project) {
        this.project = project;
        this.mockConfig = new MockConfig();
    }
    
    public static MockConfigService getInstance(Project project) {
        return project.getService(MockConfigService.class);
    }
    
    public MockConfig getConfig() {
        return mockConfig;
    }
    
    public void saveConfig() {
        // 同步写入临时配置文件，供 agent watcher 热重载
        try {
            java.io.File configFile = new java.io.File(
                System.getProperty("java.io.tmpdir") + "/mock-runner/mock-config.json");
            if (configFile.getParentFile() != null) {
                configFile.getParentFile().mkdirs();
            }
            mockConfig.rebuildMockRules();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (java.io.FileWriter writer = new java.io.FileWriter(configFile)) {
                gson.toJson(mockConfig, writer);
            }
            LOG.info("Config saved to: " + configFile.getAbsolutePath());
        } catch (Exception e) {
            LOG.error("Failed to save config to temp file: " + e.getMessage(), e);
        }
    }
    
    // 持久化状态类
    public static class State {
        public String mockConfigJson;
    }
    
    @Nullable
    @Override
    public State getState() {
        State state = new State();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        state.mockConfigJson = gson.toJson(mockConfig);
        LOG.info("Saving state: " + state.mockConfigJson);
        return state;
    }
    
    @Override
    public void loadState(@NotNull State state) {
        if (state.mockConfigJson != null && !state.mockConfigJson.isEmpty()) {
            try {
                Gson gson = new Gson();
                mockConfig = gson.fromJson(state.mockConfigJson, MockConfig.class);
                if (mockConfig == null) {
                    mockConfig = new MockConfig();
                }
                LOG.info("Loaded state: " + state.mockConfigJson);
                
                // 重建 mockRules（从 mockMethods 同步）
                mockConfig.rebuildMockRules();
                
                // 加载后更新 UI
                updateToolWindowFromConfig();
            } catch (Exception e) {
                LOG.error("Failed to load state: " + e.getMessage(), e);
                mockConfig = new MockConfig();
            }
        }
    }
    
    private void updateToolWindowFromConfig() {
        // 延迟更新 UI，确保 ToolWindow 已经初始化
        com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> {
            MockRunnerToolWindowContent toolWindow = project.getService(MockRunnerToolWindowContent.class);
            if (toolWindow != null) {
                toolWindow.refresh();
                LOG.info("Updated ToolWindow with " + mockConfig.getMockMethods().size() + " mock methods");
            }
        });
    }
    
    public void addMockMethod(String className, String methodName, String signature, String returnValue, String returnType,
                              boolean throwException, String exceptionType, String exceptionMessage) {
        // 检查是否已存在相同的mock配置
        if (mockConfig.hasMockMethod(className, methodName, signature)) {
            LOG.info("Mock method already exists, updating: " + className + "." + methodName + signature);
        }
        
        MockMethodConfig methodConfig = new MockMethodConfig();
        methodConfig.setClassName(className);
        methodConfig.setMethodName(methodName);
        methodConfig.setSignature(signature);
        methodConfig.setReturnValue(returnValue);
        methodConfig.setReturnType(returnType);
        methodConfig.setThrowException(throwException);
        methodConfig.setExceptionType(exceptionType);
        methodConfig.setExceptionMessage(exceptionMessage);
        
        mockConfig.addMockMethod(methodConfig);
        
        LOG.info("Added/Updated mock: " + className + "." + methodName + signature);
        
        // 确保 ToolWindow 可见
        com.intellij.openapi.wm.ToolWindowManager toolWindowManager = 
            com.intellij.openapi.wm.ToolWindowManager.getInstance(project);
        com.intellij.openapi.wm.ToolWindow toolWindow = toolWindowManager.getToolWindow("Mock Runner");
        if (toolWindow != null && !toolWindow.isVisible()) {
            toolWindow.show();
        }
        
        // 更新 UI
        MockRunnerToolWindowContent toolWindowContent = project.getService(MockRunnerToolWindowContent.class);
        LOG.info("ToolWindow instance: " + toolWindowContent);
        
        if (toolWindowContent != null) {
            toolWindowContent.refresh(); // 改为refresh而不是addMockMethod，避免重复添加
            LOG.info("Refreshed toolWindow");
        } else {
            LOG.error("ToolWindow is null!");
        }
        
        // 刷新编辑器，显示图标
        refreshEditors();
    }
    
    public void removeMockMethod(String className, String methodName) {
        mockConfig.removeMockMethod(className, methodName);
        
        // 更新 UI
        MockRunnerToolWindowContent toolWindow = project.getService(MockRunnerToolWindowContent.class);
        if (toolWindow != null) {
            toolWindow.refresh();
        }
        
        // 刷新编辑器
        refreshEditors();
    }
    
    public MockConfig getMockConfig() {
        return mockConfig;
    }
    
    public void clearAll() {
        mockConfig.clearAll();
        
        // 更新 UI
        MockRunnerToolWindowContent toolWindow = project.getService(MockRunnerToolWindowContent.class);
        if (toolWindow != null) {
            toolWindow.clearResults();
        }
        
        // 刷新编辑器
        refreshEditors();
    }
    
    public List<MockMethodConfig> getAllMockMethods() {
        return mockConfig.getMockMethods();
    }

    public boolean isMocked(String className, String methodName) {
        return mockConfig.getMockMethods().stream()
            .anyMatch(m -> m.getClassName().equals(className) && m.getMethodName().equals(methodName));
    }

    private void refreshEditors() {
        // 刷新所有打开的编辑器，让 LineMarker 更新
        DaemonCodeAnalyzer.getInstance(project).restart();
    }
}
