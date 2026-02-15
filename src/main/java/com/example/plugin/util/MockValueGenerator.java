package com.example.plugin.util;

import com.intellij.psi.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

/**
 * 根据返回类型自动生成 Mock 值
 */
public class MockValueGenerator {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    public static String generateMockValue(PsiType returnType) {
        if (returnType == null) {
            return "null";
        }
        
        String typeName = returnType.getPresentableText();
        
        // 基本类型
        if (typeName.equals("int") || typeName.equals("Integer")) {
            return "0";
        }
        if (typeName.equals("long") || typeName.equals("Long")) {
            return "0L";
        }
        if (typeName.equals("double") || typeName.equals("Double")) {
            return "0.0";
        }
        if (typeName.equals("float") || typeName.equals("Float")) {
            return "0.0f";
        }
        if (typeName.equals("boolean") || typeName.equals("Boolean")) {
            return "false";
        }
        if (typeName.equals("String")) {
            return "\"mock_string\"";
        }
        if (typeName.equals("void")) {
            return "void";
        }
        
        // 集合类型
        if (typeName.startsWith("List<") || typeName.startsWith("ArrayList<")) {
            return "[]";
        }
        if (typeName.startsWith("Set<") || typeName.startsWith("HashSet<")) {
            return "[]";
        }
        if (typeName.startsWith("Map<") || typeName.startsWith("HashMap<")) {
            return "{}";
        }
        
        // 数组
        if (returnType instanceof PsiArrayType) {
            return "[]";
        }
        
        // 对象类型 - 生成 JSON
        if (returnType instanceof PsiClassType) {
            PsiClass psiClass = ((PsiClassType) returnType).resolve();
            if (psiClass != null) {
                return generateObjectJson(psiClass);
            }
        }
        
        return "null";
    }
    
    private static String generateObjectJson(PsiClass psiClass) {
        Map<String, Object> jsonMap = new LinkedHashMap<>();
        
        // 获取所有字段
        PsiField[] fields = psiClass.getAllFields();
        for (PsiField field : fields) {
            // 跳过静态字段
            if (field.hasModifierProperty(PsiModifier.STATIC)) {
                continue;
            }
            
            String fieldName = field.getName();
            PsiType fieldType = field.getType();
            Object defaultValue = getDefaultValue(fieldType);
            
            jsonMap.put(fieldName, defaultValue);
        }
        
        // 如果没有字段，尝试从 getter 方法推断
        if (jsonMap.isEmpty()) {
            PsiMethod[] methods = psiClass.getAllMethods();
            for (PsiMethod method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith("get") && methodName.length() > 3) {
                    String fieldName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                    PsiType returnType = method.getReturnType();
                    if (returnType != null) {
                        jsonMap.put(fieldName, getDefaultValue(returnType));
                    }
                }
            }
        }
        
        return GSON.toJson(jsonMap);
    }
    
    private static Object getDefaultValue(PsiType type) {
        String typeName = type.getPresentableText();
        
        if (typeName.equals("int") || typeName.equals("Integer")) {
            return 0;
        }
        if (typeName.equals("long") || typeName.equals("Long")) {
            return 0;
        }
        if (typeName.equals("double") || typeName.equals("Double")) {
            return 0.0;
        }
        if (typeName.equals("float") || typeName.equals("Float")) {
            return 0.0;
        }
        if (typeName.equals("boolean") || typeName.equals("Boolean")) {
            return false;
        }
        if (typeName.equals("String")) {
            return "mock_value";
        }
        if (typeName.startsWith("List<") || typeName.startsWith("ArrayList<") || 
            typeName.startsWith("Set<") || typeName.startsWith("HashSet<")) {
            return new ArrayList<>();
        }
        if (typeName.startsWith("Map<") || typeName.startsWith("HashMap<")) {
            return new LinkedHashMap<>();
        }
        if (type instanceof PsiArrayType) {
            return new ArrayList<>();
        }
        
        // 嵌套对象
        if (type instanceof PsiClassType) {
            PsiClass psiClass = ((PsiClassType) type).resolve();
            if (psiClass != null && !psiClass.getQualifiedName().startsWith("java.")) {
                // 避免无限递归，嵌套对象返回 null
                return null;
            }
        }
        
        return null;
    }
}
