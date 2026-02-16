package test.feign;

import java.io.Serializable;

/**
 * User Response DTO
 */
public class UserResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String role;
    private String status;
    
    public UserResponse() {
    }
    
    public UserResponse(Long id, String username, String email, String phone, String role, String status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
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
    
    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
