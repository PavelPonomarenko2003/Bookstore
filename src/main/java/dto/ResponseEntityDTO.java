package dto;

public class ResponseEntityDTO<T> {

    private final T body;
    private final int status;

    public ResponseEntityDTO(T body, int status) {
        this.body = body;
        this.status = status;
    }

    public T getBody() { return body; }
    public int getStatus() { return status; }

    public static <T> ResponseEntityDTO<T> status(int status, T body) {
        return new ResponseEntityDTO<>(body, status);
    }
}
