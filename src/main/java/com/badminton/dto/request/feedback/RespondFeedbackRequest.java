package com.badminton.dto.request.feedback;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respond to feedback request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespondFeedbackRequest {

    @NotNull(message = "Feedback ID không được để trống")
    private Long feedbackId;

    @NotBlank(message = "Phản hồi không được để trống")
    @Size(min = 10, max = 2000, message = "Phản hồi phải từ 10-2000 ký tự")
    private String response;
}
