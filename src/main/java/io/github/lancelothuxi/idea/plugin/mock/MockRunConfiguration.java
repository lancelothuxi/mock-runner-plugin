package io.github.lancelothuxi.idea.plugin.mock;

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MockRunConfiguration extends RunConfigurationBase<RunProfileState> {
    
    private String myCustomParameter = "";

    protected MockRunConfiguration(@NotNull Project project, 
                                   @NotNull ConfigurationFactory factory, 
                                   @NotNull String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new SettingsEditor<MockRunConfiguration>() {
            private JPanel panel;
            private JTextField parameterField;

            @Override
            protected void resetEditorFrom(@NotNull MockRunConfiguration config) {
                if (parameterField != null) {
                    parameterField.setText(config.getCustomParameter());
                }
            }

            @Override
            protected void applyEditorTo(@NotNull MockRunConfiguration config) {
                if (parameterField != null) {
                    config.setCustomParameter(parameterField.getText());
                }
            }

            @NotNull
            @Override
            protected JComponent createEditor() {
                panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                
                JLabel label = new JLabel("Custom Parameter:");
                parameterField = new JTextField(20);
                
                panel.add(label);
                panel.add(parameterField);
                
                return panel;
            }
        };
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, 
                                    @NotNull ExecutionEnvironment environment) {
        return new CommandLineState(environment) {
            @NotNull
            @Override
            protected ProcessHandler startProcess() throws ExecutionException {
                // 这里创建实际的进程
                GeneralCommandLine commandLine = new GeneralCommandLine();
                commandLine.setExePath("java");
                commandLine.addParameter("-version");
                
                // 添加自定义参数
                if (!myCustomParameter.isEmpty()) {
                    commandLine.addParameter("-D" + myCustomParameter);
                }
                
                return new OSProcessHandler(commandLine);
            }
        };
    }

    public String getCustomParameter() {
        return myCustomParameter;
    }

    public void setCustomParameter(String customParameter) {
        this.myCustomParameter = customParameter;
    }
}
