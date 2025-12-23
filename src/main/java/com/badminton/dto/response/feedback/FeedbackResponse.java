package com.badminton.dto.response.feedback;

import com.badminton.enums.FeedbackCategory;
import com.badminton.enums.FeedbackStatus;
import com.badminton.enums.FeedbackType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Feedback response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackResponse {

    private Long id;

    private UserInfo user;
    private BranchInfo branch;

    private FeedbackType feedbackType;
    private FeedbackCategory category;
    private FeedbackStatus status;

    private String title;
    private String content;

    private Integer rating;
    private Integer serviceRating;
    private Integer facilityRating;
    private Integer cleanlinessRating;
    private Integer valueRating;
    private Integer staffRating;

    private BookingInfo booking;
    private OrderInfo order;
    private CourtInfo court;

    private List<String> images;

    private Boolean isVerifiedCustomer;
    private Boolean isPublished;
    private Boolean isAnonymous;

    private String response;
    private UserInfo respondedBy;
    private LocalDateTime respondedAt;

    private Integer helpfulCount;
    private Integer notHelpfulCount;

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
        private String avatar;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchInfo {
        private Long id;
        private String name;
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingInfo {
        private Long id;
        private String bookingNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderInfo {
        private Long id;
        private String orderNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourtInfo {
        private Long id;
        private String name;
    }
}
