package test.dubbo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Order Data Transfer Object
 */
public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long orderId;
    private Long userId;
    private String orderNo;
    private BigDecimal totalAmount;
    private String status; // PENDING, PAID, SHIPPED, COMPLETED, CANCELLED
    private Date createTime;
    private Date updateTime;
    
    public OrderDTO() {
    }
    
    public OrderDTO(Long orderId, Long userId, String orderNo, BigDecimal totalAmount, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderNo = orderNo;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createTime = new Date();
        this.updateTime = new Date();
    }
    
    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", orderNo='" + orderNo + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
