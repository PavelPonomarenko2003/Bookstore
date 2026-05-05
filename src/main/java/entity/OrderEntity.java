package entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity that provide check about every purchase
 */

public class OrderEntity extends BaseEntity{

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private BigDecimal totalPrice;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTimestamp;
    private LocalDateTime finishedTimestamp;
    private OrderStatus orderStatus;
    private Payment payment;
    private List<OrderItemEntity> listBooksInOrder = new ArrayList<>();

    public OrderEntity(
            Long userId,
            BigDecimal totalPrice,
            LocalDateTime createdTimestamp,
            LocalDateTime finishedTimestamp,
            OrderStatus orderStatus, Payment payment,
            List<OrderItemEntity> listBooksInOrder
    ) {
        super();
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.createdTimestamp = createdTimestamp;
        this.finishedTimestamp = finishedTimestamp;
        this.orderStatus = orderStatus;
        this.payment = Payment.CARD;
        this.listBooksInOrder = listBooksInOrder;
    }

    public OrderEntity() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public LocalDateTime getFinishedTimestamp() {
        return finishedTimestamp;
    }

    public void setFinishedTimestamp(LocalDateTime finishedTimestamp) {
        this.finishedTimestamp = finishedTimestamp;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public List<OrderItemEntity> getListBooksInOrder() {
        return listBooksInOrder;
    }

    public void setListBooksInOrder(List<OrderItemEntity> listBooksInOrder) {
        this.listBooksInOrder = listBooksInOrder;
    }
}
