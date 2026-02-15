package com.example.plugin.marker;

import com.example.plugin.service.MockConfigService;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 在被 Mock 的方法旁边显示图标
 */
public class MockedMethodLineMarkerProvider implements LineMarkerProvider {
    
    @Nullable
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        // 只处理方法标识符
        if (!(element instanceof PsiIdentifier)) {
            return null;
        }
        
        PsiElement parent = element.getParent();
        if (!(parent instanceof PsiMethod)) {
            return null;
        }
        
        PsiMethod method = (PsiMethod) parent;
        PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            return null;
        }
        
        // 检查这个方法是否被 Mock 了
        Project project = element.getProject();
        MockConfigService service = project.getService(MockConfigService.class);
        
        String className = containingClass.getQualifiedName();
        String methodName = method.getName();
        
        if (service != null && service.isMocked(className, methodName)) {
            // 创建图标标记
            return new LineMarkerInfo<>(
                element,
                element.getTextRange(),
                AllIcons.Debugger.Db_muted_breakpoint,  // 使用一个合适的图标
                psiElement -> "This method is mocked",
                null,
                GutterIconRenderer.Alignment.LEFT,
                () -> "Mocked Method"
            );
        }
        
        return null;
    }
}
