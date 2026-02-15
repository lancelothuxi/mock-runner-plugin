package com.example.plugin.mock;

import java.io.Serializable;

public class MockMethodConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String className;
    private String methodName;
    private String signature;
    private String returnValue;
    private boolean enabled = true;
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public String getSignature() {
        return signature;
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public String getReturnValue() {
        return returnValue;
    }
    
    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getFullMethodName() {
        return className + "." + methodName + signature;
    }
}
