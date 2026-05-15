package bookstore.servlet.controller;

import bookstore.dto.MessageResponse;
import bookstore.entity.StockEntity;
import bookstore.service.interfaces.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<List<StockEntity>> getAllStocks() {
        List<StockEntity> stocks = stockService.findAllBooks();
        return ResponseEntity.ok(stocks);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> updateStock(
            @RequestParam("bookId") Long bookId,
            @RequestParam("amount") Integer amount
    ) {

        stockService.updateQuantity(bookId, amount);

        return ResponseEntity.ok(
                new MessageResponse("Stock updated successfully for book ID: " + bookId)
        );
    }
}