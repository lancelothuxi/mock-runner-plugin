package com.example.plugin;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MyRunConfiguration extends RunConfigurationBase<RunProfileState> {
    
    private String myCustomParameter = "";

    protected MyRunConfiguration(@NotNull Project project, 
                                 @NotNull ConfigurationFactory factory, 
                                 @NotNull String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new SettingsEditor<MyRunConfiguration>() {
            private JPanel panel;
            private JTextField parameterField;

            @Override
            protected void resetEditorFrom(@NotNull MyRunConfiguration config) {
                if (parameterField != null) {
                    parameterField.setText(config.getCustomParameter());
                }
            }

            @Override
            protected void applyEditorTo(@NotNull MyRunConfiguration config) {
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
