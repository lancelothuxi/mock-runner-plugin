package com.example.plugin;

import com.intellij.execution.Executor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.wm.ToolWindowId;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MyDebugExecutor extends Executor {
    public static final String EXECUTOR_ID = "MyDebugExecutor";
    
    @Override
    public @NotNull String getToolWindowId() {
        return ToolWindowId.DEBUG;
    }
    
    @Override
    public @NotNull Icon getToolWindowIcon() {
        return AllIcons.Actions.StartDebugger;
    }
    
    @Override
    public @NotNull Icon getIcon() {
        return AllIcons.Actions.StartDebugger;
    }
    
    @Override
    public Icon getDisabledIcon() {
        return null;
    }
    
    @Override
    public String getDescription() {
        return "Debug with Mock Runner";
    }
    
    @Override
    public @NotNull String getActionName() {
        return "MyDebugRun";
    }
    
    @Override
    public @NotNull String getId() {
        return EXECUTOR_ID;
    }
    
    @Override
    public @NotNull String getStartActionText() {
        return "Debug with Mock Runner";
    }
    
    @Override
    public String getContextActionId() {
        return "DebugClassMyCustom";
    }
    
    @Override
    public String getHelpId() {
        return null;
    }
}
