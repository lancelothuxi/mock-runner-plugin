package io.github.lancelothuxi.idea.plugin.mock.mock;

import java.io.Serializable;

public class MockMethodConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String className;
    private String methodName;
    private String signature;
    private String returnValue;
    private String returnType;
    private boolean enabled = true;
    private boolean throwException = false;
    private String exceptionType = "java.lang.RuntimeException";
    private String exceptionMessage = "Mocked exception";
    
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
    
    public String getReturnType() {
        return returnType;
    }
    
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isThrowException() {
        return throwException;
    }
    
    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }
    
    public String getExceptionType() {
        return exceptionType;
    }
    
    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }
    
    public String getExceptionMessage() {
        return exceptionMessage;
    }
    
    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
    
    public String getFullMethodName() {
        return className + "." + methodName + signature;
    }
}
