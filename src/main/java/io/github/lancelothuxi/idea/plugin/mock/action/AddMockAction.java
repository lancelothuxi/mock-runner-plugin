package io.github.lancelothuxi.idea.plugin.mock.action;

import io.github.lancelothuxi.idea.plugin.mock.service.MockConfigService;
import io.github.lancelothuxi.idea.plugin.mock.ui.AddMockDialog;
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
        PsiType returnType = method.getReturnType();
        
        // 检查是否是 void 方法
        if (returnType == null || returnType.equals(PsiType.VOID)) {
            Messages.showWarningDialog(
                project,
                "Cannot mock void methods. Only methods with return values can be mocked.",
                "Cannot Mock Void Method"
            );
            return;
        }
        
        // 使用新的对话框
        AddMockDialog dialog = new AddMockDialog(project, className, methodName, signature, returnType);
        if (dialog.showAndGet()) {
            String mockValue = dialog.getMockValue();
            boolean throwException = dialog.isThrowException();
            String exceptionType = dialog.getExceptionType();
            String exceptionMessage = dialog.getExceptionMessage();
            
            // 获取完整的返回类型字符串（包含泛型）
            String returnTypeString = returnType != null ? returnType.getCanonicalText() : "void";
            
            // 添加到 Mock 配置
            MockConfigService service = project.getService(MockConfigService.class);
            service.addMockMethod(className, methodName, signature, mockValue, returnTypeString, 
                                throwException, exceptionType, exceptionMessage);
            
            Messages.showInfoMessage(
                project,
                "Mock added successfully!\nCheck the 'Mock Runner' panel on the right.",
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
