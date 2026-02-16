package test.dubbo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Create Order Request
 */
public class CreateOrderRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long userId;
    private List<OrderItem> items;
    private String shippingAddress;
    private String paymentMethod;
    
    public CreateOrderRequest() {
    }
    
    public CreateOrderRequest(Long userId, List<OrderItem> items, String shippingAddress, String paymentMethod) {
        this.userId = userId;
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public static class OrderItem implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        
        public OrderItem() {
        }
        
        public OrderItem(Long productId, String productName, Integer quantity, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }
        
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public String getProductName() {
            return productName;
        }
        
        public void setProductName(String productName) {
            this.productName = productName;
        }
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        
        public BigDecimal getPrice() {
            return price;
        }
        
        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}
