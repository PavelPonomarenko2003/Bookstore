package bookstore.servlet.controller;

import bookstore.service.impl.BookRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final BookRecommendationService recommendationService;

    public RecommendationController(BookRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getRecommendations(@RequestParam("userId") Long userId) {
        List<String> recommendations = recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }
}
