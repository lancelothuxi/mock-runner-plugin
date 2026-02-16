package io.github.lancelothuxi.idea.plugin.mock.util;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

/**
 * 根据返回类型自动生成 Mock 值
 */
public class MockValueGenerator {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Set<String> PROCESSING_TYPES = new HashSet<>(); // 防止循环引用
    
    public static String generateMockValue(PsiType returnType) {
        if (returnType == null) {
            return "null";
        }
        
        PROCESSING_TYPES.clear();
        return generateMockValueInternal(returnType);
    }
    
    private static String generateMockValueInternal(PsiType returnType) {
        String typeName = returnType.getPresentableText();
        
        // 基本类型
        if (typeName.equals("int") || typeName.equals("Integer")) {
            return "1";
        }
        if (typeName.equals("long") || typeName.equals("Long")) {
            return "1L";
        }
        if (typeName.equals("double") || typeName.equals("Double")) {
            return "1.0";
        }
        if (typeName.equals("float") || typeName.equals("Float")) {
            return "1.0f";
        }
        if (typeName.equals("boolean") || typeName.equals("Boolean")) {
            return "true";
        }
        if (typeName.equals("String")) {
            return "\"sample_string\"";
        }
        if (typeName.equals("void")) {
            return "void";
        }
        
        // 处理泛型集合类型
        if (returnType instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) returnType;
            PsiClass psiClass = classType.resolve();
            
            if (psiClass != null) {
                String qualifiedName = psiClass.getQualifiedName();
                
                // List类型
                if ("java.util.List".equals(qualifiedName) || 
                    "java.util.ArrayList".equals(qualifiedName) ||
                    "java.util.LinkedList".equals(qualifiedName)) {
                    return generateListMockValue(classType);
                }
                
                // Set类型
                if ("java.util.Set".equals(qualifiedName) || 
                    "java.util.HashSet".equals(qualifiedName) ||
                    "java.util.LinkedHashSet".equals(qualifiedName)) {
                    return generateListMockValue(classType);
                }
                
                // Map类型
                if ("java.util.Map".equals(qualifiedName) || 
                    "java.util.HashMap".equals(qualifiedName) ||
                    "java.util.LinkedHashMap".equals(qualifiedName)) {
                    return generateMapMockValue(classType);
                }
                
                // 自定义对象类型
                if (qualifiedName != null && !qualifiedName.startsWith("java.")) {
                    return generateObjectJson(psiClass);
                }
            }
        }
        
        // 数组
        if (returnType instanceof PsiArrayType) {
            PsiArrayType arrayType = (PsiArrayType) returnType;
            PsiType componentType = arrayType.getComponentType();
            Object sampleElement = getDefaultValueObject(componentType);
            List<Object> arrayList = new ArrayList<>();
            arrayList.add(sampleElement);
            return GSON.toJson(arrayList);
        }
        
        return "null";
    }
    
    private static String generateListMockValue(PsiClassType listType) {
        PsiType[] typeParameters = listType.getParameters();
        if (typeParameters.length > 0) {
            PsiType elementType = typeParameters[0];
            Object sampleElement = getDefaultValueObject(elementType);
            
            List<Object> mockList = new ArrayList<>();
            mockList.add(sampleElement);
            
            // 如果是复杂对象，添加第二个示例以显示数组结构
            if (sampleElement instanceof Map) {
                mockList.add(sampleElement);
            }
            
            return GSON.toJson(mockList);
        }
        return "[]";
    }
    
    private static String generateMapMockValue(PsiClassType mapType) {
        PsiType[] typeParameters = mapType.getParameters();
        if (typeParameters.length >= 2) {
            PsiType keyType = typeParameters[0];
            PsiType valueType = typeParameters[1];
            
            Object sampleKey = getDefaultValueObject(keyType);
            Object sampleValue = getDefaultValueObject(valueType);
            
            Map<Object, Object> mockMap = new LinkedHashMap<>();
            mockMap.put(sampleKey, sampleValue);
            
            return GSON.toJson(mockMap);
        }
        return "{}";
    }
    
    private static String generateObjectJson(PsiClass psiClass) {
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName == null || PROCESSING_TYPES.contains(qualifiedName)) {
            return "null"; // 防止循环引用
        }
        
        PROCESSING_TYPES.add(qualifiedName);
        
        try {
            Map<String, Object> jsonMap = new LinkedHashMap<>();
            
            // 获取所有字段
            PsiField[] fields = psiClass.getAllFields();
            for (PsiField field : fields) {
                // 跳过静态字段和常量
                if (field.hasModifierProperty(PsiModifier.STATIC) || 
                    field.hasModifierProperty(PsiModifier.FINAL)) {
                    continue;
                }
                
                String fieldName = field.getName();
                PsiType fieldType = field.getType();
                Object defaultValue = getDefaultValueObject(fieldType);
                
                jsonMap.put(fieldName, defaultValue);
            }
            
            // 如果没有字段，尝试从 getter 方法推断
            if (jsonMap.isEmpty()) {
                PsiMethod[] methods = psiClass.getAllMethods();
                for (PsiMethod method : methods) {
                    String methodName = method.getName();
                    if ((methodName.startsWith("get") && methodName.length() > 3) ||
                        (methodName.startsWith("is") && methodName.length() > 2)) {
                        
                        String fieldName;
                        if (methodName.startsWith("get")) {
                            fieldName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                        } else {
                            fieldName = Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
                        }
                        
                        PsiType returnType = method.getReturnType();
                        if (returnType != null && !jsonMap.containsKey(fieldName)) {
                            jsonMap.put(fieldName, getDefaultValueObject(returnType));
                        }
                    }
                }
            }
            
            return GSON.toJson(jsonMap);
        } finally {
            PROCESSING_TYPES.remove(qualifiedName);
        }
    }
    
    private static Object getDefaultValueObject(PsiType type) {
        String typeName = type.getPresentableText();
        
        if (typeName.equals("int") || typeName.equals("Integer")) {
            return 1;
        }
        if (typeName.equals("long") || typeName.equals("Long")) {
            return 1L;
        }
        if (typeName.equals("double") || typeName.equals("Double")) {
            return 1.0;
        }
        if (typeName.equals("float") || typeName.equals("Float")) {
            return 1.0f;
        }
        if (typeName.equals("boolean") || typeName.equals("Boolean")) {
            return true;
        }
        if (typeName.equals("String")) {
            return "sample_value";
        }
        
        // 处理泛型集合
        if (type instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) type;
            PsiClass psiClass = classType.resolve();
            
            if (psiClass != null) {
                String qualifiedName = psiClass.getQualifiedName();
                
                if ("java.util.List".equals(qualifiedName) || 
                    "java.util.ArrayList".equals(qualifiedName)) {
                    PsiType[] params = classType.getParameters();
                    if (params.length > 0) {
                        List<Object> list = new ArrayList<>();
                        list.add(getDefaultValueObject(params[0]));
                        return list;
                    }
                    return new ArrayList<>();
                }
                
                if ("java.util.Map".equals(qualifiedName) || 
                    "java.util.HashMap".equals(qualifiedName)) {
                    return new LinkedHashMap<>();
                }
                
                // 自定义对象
                if (qualifiedName != null && !qualifiedName.startsWith("java.") && 
                    !PROCESSING_TYPES.contains(qualifiedName)) {
                    
                    PROCESSING_TYPES.add(qualifiedName);
                    try {
                        Map<String, Object> objMap = new LinkedHashMap<>();
                        
                        // 简化版本，只获取基本字段
                        PsiField[] fields = psiClass.getAllFields();
                        for (PsiField field : fields) {
                            if (!field.hasModifierProperty(PsiModifier.STATIC) && 
                                !field.hasModifierProperty(PsiModifier.FINAL)) {
                                
                                String fieldName = field.getName();
                                PsiType fieldType = field.getType();
                                
                                // 避免深度嵌套
                                if (fieldType instanceof PsiClassType) {
                                    PsiClass fieldClass = ((PsiClassType) fieldType).resolve();
                                    if (fieldClass != null && fieldClass.getQualifiedName() != null &&
                                        !fieldClass.getQualifiedName().startsWith("java.")) {
                                        objMap.put(fieldName, null); // 嵌套对象设为null
                                        continue;
                                    }
                                }
                                
                                objMap.put(fieldName, getDefaultValueObject(fieldType));
                            }
                        }
                        
                        return objMap;
                    } finally {
                        PROCESSING_TYPES.remove(qualifiedName);
                    }
                }
            }
        }
        
        if (type instanceof PsiArrayType) {
            PsiArrayType arrayType = (PsiArrayType) type;
            List<Object> list = new ArrayList<>();
            list.add(getDefaultValueObject(arrayType.getComponentType()));
            return list;
        }
        
        return null;
    }
}
