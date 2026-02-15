package com.example.plugin;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

public class MockRunConfigurationProducer extends LazyRunConfigurationProducer<MockRunConfiguration> {
    
    @NotNull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return new MockRunConfigurationFactory(new MockRunnerConfigurationType());
    }
    
    @Override
    protected boolean setupConfigurationFromContext(@NotNull MockRunConfiguration configuration,
                                                      @NotNull ConfigurationContext context,
                                                      @NotNull Ref<PsiElement> sourceElement) {
        PsiElement element = context.getPsiLocation();
        if (element == null) {
            return false;
        }
        
        // 检查是否是 main 方法
        PsiMethod method = findMainMethod(element);
        if (method != null) {
            configuration.setName("Run with My Custom Runner: " + method.getContainingClass().getName());
            sourceElement.set(method);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean isConfigurationFromContext(@NotNull MockRunConfiguration configuration,
                                                @NotNull ConfigurationContext context) {
        PsiElement element = context.getPsiLocation();
        if (element == null) {
            return false;
        }
        
        PsiMethod method = findMainMethod(element);
        return method != null;
    }
    
    private PsiMethod findMainMethod(PsiElement element) {
        while (element != null) {
            if (element instanceof PsiMethod) {
                PsiMethod method = (PsiMethod) element;
                if (isMainMethod(method)) {
                    return method;
                }
            }
            element = element.getParent();
        }
        return null;
    }
    
    private boolean isMainMethod(PsiMethod method) {
        if (!"main".equals(method.getName())) {
            return false;
        }
        if (!method.hasModifierProperty(com.intellij.psi.PsiModifier.PUBLIC) || 
            !method.hasModifierProperty(com.intellij.psi.PsiModifier.STATIC)) {
            return false;
        }
        return true;
    }
}
