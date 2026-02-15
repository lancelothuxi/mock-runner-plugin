package com.example.plugin.service;

import com.example.plugin.mock.MockConfig;
import com.example.plugin.mock.MockMethodConfig;
import com.example.plugin.ui.MyRunnerToolWindowContent;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.project.Project;

import java.util.List;

public class MockConfigService {
    private final Project project;
    private final MockConfig mockConfig;
    
    public MockConfigService(Project project) {
        this.project = project;
        this.mockConfig = new MockConfig();
    }
    
    public void addMockMethod(String className, String methodName, String signature, String returnValue) {
        MockMethodConfig methodConfig = new MockMethodConfig();
        methodConfig.setClassName(className);
        methodConfig.setMethodName(methodName);
        methodConfig.setSignature(signature);
        methodConfig.setReturnValue(returnValue);
        
        mockConfig.addMockMethod(methodConfig);
        
        System.out.println("[MockConfigService] Added mock: " + className + "." + methodName);
        
        // 确保 ToolWindow 可见
        com.intellij.openapi.wm.ToolWindowManager toolWindowManager = 
            com.intellij.openapi.wm.ToolWindowManager.getInstance(project);
        com.intellij.openapi.wm.ToolWindow toolWindow = toolWindowManager.getToolWindow("My Runner");
        if (toolWindow != null && !toolWindow.isVisible()) {
            toolWindow.show();
        }
        
        // 更新 UI
        MyRunnerToolWindowContent toolWindowContent = project.getService(MyRunnerToolWindowContent.class);
        System.out.println("[MockConfigService] ToolWindow instance: " + toolWindowContent);
        
        if (toolWindowContent != null) {
            toolWindowContent.addMockMethod(className, methodName, signature, returnValue);
            System.out.println("[MockConfigService] Called addMockMethod on toolWindow");
        } else {
            System.err.println("[MockConfigService] ToolWindow is null!");
        }
        
        // 刷新编辑器，显示图标
        refreshEditors();
    }
    
    public void removeMockMethod(String className, String methodName) {
        mockConfig.removeMockMethod(className, methodName);
        
        // 更新 UI
        MyRunnerToolWindowContent toolWindow = project.getService(MyRunnerToolWindowContent.class);
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
        MyRunnerToolWindowContent toolWindow = project.getService(MyRunnerToolWindowContent.class);
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
