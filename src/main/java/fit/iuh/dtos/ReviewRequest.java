package fit.iuh.dtos;

import lombok.Data;

@Data
public class ReviewRequest {
   private Long gameId;
   private Integer rating;
   private String comment;
}
