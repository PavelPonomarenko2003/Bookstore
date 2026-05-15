package bookstore.servlet.controller;

import bookstore.dto.MessageResponse;
import bookstore.dto.OrderRequestDTO;
import bookstore.service.impl.BookRecommendationService;
import bookstore.service.interfaces.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import bookstore.dto.OrderResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final BookRecommendationService recService;

    public OrderController(OrderService orderService, BookRecommendationService recService) {
        this.orderService = orderService;
        this.recService = recService;
    }

    @PostMapping("/create")
    public ResponseEntity<MessageResponse> createOrder(@RequestBody OrderRequestDTO dto) {
        orderService.createOrder(dto.getUserId(), dto.getBookTitle(), dto.getQuantity());

        recService.addPurchase(dto.getUserId(), dto.getBookTitle());
        List<String> recommendations = recService.getRecommendations(dto.getUserId());

        String message = "Order created! Your recommendations: " + recommendations;
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(message));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<MessageResponse> completeOrder(@PathVariable("id") Long id) {
        orderService.completeOrder(id);
        return ResponseEntity.ok(new MessageResponse("Order status updated successfully!"));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<MessageResponse> cancelOrder(@PathVariable("id") Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok(new MessageResponse("Order status updated successfully!"));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getSortedOrders(
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "3") int size
    ) {
        List<OrderResponseDTO> orders = orderService.getSortedOrders(sortBy, page, size);
        return ResponseEntity.ok(orders);
    }
}
