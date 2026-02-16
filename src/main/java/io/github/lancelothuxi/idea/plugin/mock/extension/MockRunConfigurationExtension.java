package io.github.lancelothuxi.idea.plugin.mock.extension;

import io.github.lancelothuxi.idea.plugin.mock.mock.MockConfig;
import io.github.lancelothuxi.idea.plugin.mock.service.MockConfigService;
import com.google.gson.Gson;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;

/**
 * 扩展所有 Java Run Configuration，在执行前添加 Mock Agent
 */
public class MockRunConfigurationExtension extends com.intellij.execution.RunConfigurationExtension {
    
    private static final Logger LOG = Logger.getInstance(MockRunConfigurationExtension.class);
    
    @Override
    public <T extends RunConfigurationBase<?>> void updateJavaParameters(
            @NotNull T configuration,
            @NotNull JavaParameters params,
            RunnerSettings runnerSettings) throws ExecutionException {
        
        LOG.info("========== updateJavaParameters CALLED ==========");
        LOG.info("Configuration: " + configuration.getName());
        LOG.info("Type: " + configuration.getType().getDisplayName());
        
        try {
            // 获取 Mock 配置
            MockConfigService mockConfigService = configuration.getProject().getService(MockConfigService.class);
            if (mockConfigService == null) {
                LOG.error("MockConfigService is null!");
                return;
            }
            
            MockConfig mockConfig = mockConfigService.getMockConfig();
            if (mockConfig == null || mockConfig.getMockMethods().isEmpty()) {
                LOG.info("No mock methods configured, skipping agent");
                return;
            }
            
            LOG.info("Found " + mockConfig.getMockMethods().size() + " mock methods");
            
            // 保存 Mock 配置到临时文件
            File configFile = saveMockConfig(mockConfig);
            LOG.info("Mock config saved to: " + configFile.getAbsolutePath());
            
            // 获取 agent jar 路径
            String agentJarPath = getAgentJarPath();
            if (agentJarPath == null) {
                LOG.error("Agent jar not found!");
                return;
            }
            
            // 添加 javaagent 参数
            String agentArg = "-javaagent:" + agentJarPath + "=" + configFile.getAbsolutePath();
            params.getVMParametersList().add(agentArg);
            
            LOG.info("========== Agent added successfully ==========");
            LOG.info("Agent argument: " + agentArg);
            
        } catch (Exception e) {
            LOG.error("Error adding agent: " + e.getMessage(), e);
        }
    }
    
    private File saveMockConfig(MockConfig mockConfig) throws Exception {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "mock-runner");
        tempDir.mkdirs();
        
        File configFile = new File(tempDir, "mock-config.json");
        
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(mockConfig, writer);
        }
        
        return configFile;
    }
    
    private String getAgentJarPath() {
        try {
            // 方法1: 通过 PluginManager 获取插件路径
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
    
    @Override
    protected void readExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element)
            throws InvalidDataException {
        // 不需要保存额外的配置
    }
    
    @Override
    protected void writeExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element)
            throws WriteExternalException {
        // 不需要保存额外的配置
    }
    
    @Nullable
    @Override
    protected String getEditorTitle() {
        return "Mock Runner";
    }
    
    @Override
    public boolean isApplicableFor(@NotNull RunConfigurationBase configuration) {
        // 适用于所有 Java Application 配置
        return configuration.getType().getDisplayName().contains("Application");
    }
    
    @Nullable
    @Override
    protected <P extends RunConfigurationBase<?>> SettingsEditor<P> createEditor(@NotNull P configuration) {
        // 不需要额外的 UI
        return null;
    }
}
