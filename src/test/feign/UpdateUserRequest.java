package test.feign;

import java.io.Serializable;

/**
 * Update User Request
 */
public class UpdateUserRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String email;
    private String phone;
    private String role;
    private String status;
    
    public UpdateUserRequest() {
    }
    
    public UpdateUserRequest(String email, String phone, String role, String status) {
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
