package com.badminton.dto.request.feedback;

import com.badminton.enums.FeedbackCategory;
import com.badminton.enums.FeedbackType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Create feedback request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeedbackRequest {

    @NotNull(message = "Chi nhánh không được để trống")
    private Long branchId;

    @NotNull(message = "Loại feedback không được để trống")
    private FeedbackType feedbackType;

    @NotNull(message = "Danh mục không được để trống")
    private FeedbackCategory category;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(min = 5, max = 200, message = "Tiêu đề phải từ 5-200 ký tự")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    @Size(min = 10, max = 5000, message = "Nội dung phải từ 10-5000 ký tự")
    private String content;

    @NotNull(message = "Đánh giá không được để trống")
    @Min(value = 1, message = "Đánh giá phải từ 1-5")
    @Max(value = 5, message = "Đánh giá phải từ 1-5")
    private Integer rating;

    // Detailed ratings
    private Integer serviceRating;
    private Integer facilityRating;
    private Integer cleanlinessRating;
    private Integer valueRating;
    private Integer staffRating;

    // Reference
    private Long bookingId;
    private Long orderId;
    private Long courtId;

    private List<String> images;

    private Boolean isAnonymous;
}
