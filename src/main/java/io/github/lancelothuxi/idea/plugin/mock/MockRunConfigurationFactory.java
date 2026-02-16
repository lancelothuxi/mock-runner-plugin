package io.github.lancelothuxi.idea.plugin.mock;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MockRunConfigurationFactory extends ConfigurationFactory {
    
    protected MockRunConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public String getId() {
        return "MY_CUSTOM_RUNNER_FACTORY";
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new MockRunConfiguration(project, this, "Mock Custom Run");
    }
}
