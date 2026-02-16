package io.github.lancelothuxi.idea.plugin.mock;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MockRunnerConfigurationType implements ConfigurationType {
    
    @NotNull
    @Override
    public String getDisplayName() {
        return "My Custom Runner";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Run or debug with custom settings";
    }

    @Override
    public Icon getIcon() {
        return AllIcons.RunConfigurations.Application;
    }

    @NotNull
    @Override
    public String getId() {
        return "MY_CUSTOM_RUNNER_CONFIGURATION";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new MockRunConfigurationFactory(this)};
    }
}
