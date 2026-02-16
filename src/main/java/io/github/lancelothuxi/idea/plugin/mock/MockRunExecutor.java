package io.github.lancelothuxi.idea.plugin.mock;

import com.intellij.execution.Executor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.wm.ToolWindowId;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MockRunExecutor extends Executor {
    public static final String EXECUTOR_ID = "MyCustomExecutor";
    
    @Override
    public @NotNull String getToolWindowId() {
        return ToolWindowId.RUN;
    }
    
    @Override
    public @NotNull Icon getToolWindowIcon() {
        return AllIcons.Actions.Execute;
    }
    
    @Override
    public @NotNull Icon getIcon() {
        return AllIcons.Actions.Execute;
    }
    
    @Override
    public Icon getDisabledIcon() {
        return null;
    }
    
    @Override
    public String getDescription() {
        return "Run with My Custom Runner";
    }
    
    @Override
    public @NotNull String getActionName() {
        return "MyCustomRun";
    }
    
    @Override
    public @NotNull String getId() {
        return EXECUTOR_ID;
    }
    
    @Override
    public @NotNull String getStartActionText() {
        return "Run with My Custom Runner";
    }
    
    @Override
    public String getContextActionId() {
        return "RunClassMyCustom";
    }
    
    @Override
    public String getHelpId() {
        return null;
    }
}
