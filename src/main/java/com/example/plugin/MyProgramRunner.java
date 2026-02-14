package com.example.plugin;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyProgramRunner extends GenericProgramRunner {
    
    private static final String RUNNER_ID = "MyCustomProgramRunner";

    @NotNull
    @Override
    public String getRunnerId() {
        return RUNNER_ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        // 支持 Run 和 Debug
        return (DefaultRunExecutor.EXECUTOR_ID.equals(executorId) || 
                DefaultDebugExecutor.EXECUTOR_ID.equals(executorId)) 
                && profile instanceof MyRunConfiguration;
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, 
                                             @NotNull ExecutionEnvironment environment) 
            throws ExecutionException {
        
        // 在这里可以添加自定义的启动逻辑
        System.out.println("Running with My Custom Runner!");
        
        // 调用默认的执行逻辑
        return super.doExecute(state, environment);
    }
}
