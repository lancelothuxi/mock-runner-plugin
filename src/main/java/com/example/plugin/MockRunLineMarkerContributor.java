package com.example.plugin;

import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MockRunLineMarkerContributor extends RunLineMarkerContributor {
    
    @Override
    public @Nullable Info getInfo(@NotNull PsiElement element) {
        // 只在方法名标识符上显示
        if (!(element instanceof PsiIdentifier)) {
            return null;
        }
        
        PsiElement parent = element.getParent();
        if (!(parent instanceof PsiMethod)) {
            return null;
        }
        
        PsiMethod method = (PsiMethod) parent;
        if (isMainMethod(method)) {
            return new Info(
                AllIcons.RunConfigurations.TestState.Run,
                ExecutorAction.getActions(0),
                element1 -> "Run with My Custom Runner"
            );
        }
        
        return null;
    }
    
    private boolean isMainMethod(PsiMethod method) {
        if (!"main".equals(method.getName())) {
            return false;
        }
        if (!method.hasModifierProperty(PsiModifier.PUBLIC) || 
            !method.hasModifierProperty(PsiModifier.STATIC)) {
            return false;
        }
        return true;
    }
}
