package bookstore.dto;

public class OrderRequestDTO {
    private Long userId;
    private String bookTitle;
    private Integer quantity;

    public OrderRequestDTO() {
    }

    public OrderRequestDTO(Long userId, String bookTitle, Integer quantity) {
        this.userId = userId;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
