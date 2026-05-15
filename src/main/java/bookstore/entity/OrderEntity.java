package bookstore.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity that provide check about every purchase
 */
@Entity
@Table(name = "orders")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderEntity extends BaseEntity{

    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTimestamp;

    @Column(name = "finished_at")
    private LocalDateTime finishedTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private Payment payment = Payment.CARD;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemEntity> listBooksInOrder = new ArrayList<>();

    public OrderEntity(
            UserEntity user,
            BigDecimal totalPrice,
            LocalDateTime createdTimestamp,
            LocalDateTime finishedTimestamp,
            OrderStatus orderStatus, Payment payment,
            List<OrderItemEntity> listBooksInOrder
    ) {
        super();
        this.user = user;
        this.totalPrice = totalPrice;
        this.createdTimestamp = createdTimestamp;
        this.finishedTimestamp = finishedTimestamp;
        this.orderStatus = orderStatus;
        this.payment = payment;
        this.listBooksInOrder = listBooksInOrder;
    }

    public void addOrderItem(OrderItemEntity item) {
        this.listBooksInOrder.add(item);
        item.setOrder(this);
    }

    public OrderEntity() {
        super();
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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
