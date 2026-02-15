package com.example.plugin.action;

import com.example.plugin.mock.MockConfig;
import com.example.plugin.mock.MockMethodConfig;
import com.example.plugin.service.MockConfigService;
import com.example.plugin.util.MockValueGenerator;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class AddMockAction extends AnAction {
    
    public AddMockAction() {
        super("Add Mock for Method");
    }
    
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;
        
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        
        if (editor == null || psiFile == null) return;
        
        // 获取当前光标位置的方法
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        
        if (method == null) {
            Messages.showErrorDialog(project, "Please place cursor on a method", "Add Mock");
            return;
        }
        
        // 获取方法信息
        PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) return;
        
        String className = containingClass.getQualifiedName();
        String methodName = method.getName();
        String signature = getMethodSignature(method);
        
        // 自动生成 Mock 返回值
        PsiType returnType = method.getReturnType();
        String generatedValue = MockValueGenerator.generateMockValue(returnType);
        
        // 弹出对话框，显示自动生成的值，用户可以编辑
        String mockValue = Messages.showInputDialog(
            project,
            "Auto-generated mock value (you can edit):\n\n" + 
            "Method: " + className + "." + methodName + signature + "\n" +
            "Return Type: " + (returnType != null ? returnType.getPresentableText() : "void"),
            "Add Mock Configuration",
            Messages.getQuestionIcon(),
            generatedValue,  // 默认值
            null
        );
        
        if (mockValue != null && !mockValue.trim().isEmpty()) {
            // 添加到 Mock 配置
            MockConfigService service = project.getService(MockConfigService.class);
            service.addMockMethod(className, methodName, signature, mockValue);
            
            Messages.showInfoMessage(
                project,
                "Mock added successfully!\nCheck the 'My Runner' panel on the right.",
                "Success"
            );
        }
    }
    
    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        
        boolean enabled = project != null && editor != null && psiFile != null;
        e.getPresentation().setEnabledAndVisible(enabled);
    }
    
    private String getMethodSignature(PsiMethod method) {
        StringBuilder sb = new StringBuilder("(");
        PsiParameter[] parameters = method.getParameterList().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) sb.append(", ");
            PsiType type = parameters[i].getType();
            sb.append(type.getPresentableText());
        }
        sb.append(")");
        return sb.toString();
    }
}
