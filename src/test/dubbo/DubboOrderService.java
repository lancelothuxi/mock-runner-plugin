package test.dubbo;

import java.util.List;

/**
 * Simulated Dubbo Service Interface
 * In real Dubbo, this would be annotated with @Service or @DubboService
 */
public interface DubboOrderService {
    
    /**
     * Get order by ID
     */
    OrderDTO getOrderById(Long orderId);
    
    /**
     * Get orders by user ID
     */
    List<OrderDTO> getOrdersByUserId(Long userId);
    
    /**
     * Create new order
     */
    OrderDTO createOrder(CreateOrderRequest request);
    
    /**
     * Cancel order
     */
    boolean cancelOrder(Long orderId, String reason);
    
    /**
     * Get order count for user
     */
    int getOrderCount(Long userId);
}
