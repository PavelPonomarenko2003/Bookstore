package exception;

import entity.OrderStatus;

public class StatusChangingException extends RuntimeException {

    public StatusChangingException(OrderStatus status) {
        super("U can't change status: ");
    }

    public StatusChangingException(Throwable cause) {
        super("U can't change that status!", cause);
    }
}
