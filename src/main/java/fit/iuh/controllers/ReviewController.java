package fit.iuh.controllers;

import fit.iuh.dtos.ReviewDto;
import fit.iuh.dtos.ReviewRequest;
import fit.iuh.services.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/games/reviews")
public class ReviewController {
   private final ReviewService reviewService;

   public ReviewController(ReviewService reviewService) {
      this.reviewService = reviewService;
   }

   @GetMapping("/{id}")
   public ResponseEntity<Page<ReviewDto>> getReviewByGameId(
           @PathVariable Long id,
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "5") int size,
           @RequestParam(defaultValue = "newest") String sort,
           @RequestParam(required = false) Integer rating
   ) {
      Sort sortObj = sort.equals("oldest")
              ? Sort.by("createdAt").ascending()
              : Sort.by("createdAt").descending();

      Pageable pageable = PageRequest.of(page, size, sortObj);

      Page<ReviewDto> reviewPage = reviewService.getReviewsByGameId(id, rating, pageable);

      return ResponseEntity.ok(reviewPage);
   }

   @PostMapping("/add")
   public ResponseEntity<String> addReview(@RequestBody ReviewRequest request) {
      String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
      reviewService.addReview(request, currentUsername);
      return ResponseEntity.ok("Thêm đánh giá thành công!");
   }
}
