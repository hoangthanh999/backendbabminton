package com.badminton.entity.court;

import com.badminton.entity.base.BaseEntity;
import com.badminton.enums.ImageType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "court_images", indexes = {
        @Index(name = "idx_court", columnList = "court_id"),
        @Index(name = "idx_primary", columnList = "is_primary"),
        @Index(name = "idx_type", columnList = "image_type"),
        @Index(name = "idx_order", columnList = "display_order"),
        @Index(name = "idx_court_primary", columnList = "court_id, is_primary"),
        @Index(name = "idx_cloudinary_public_id", columnList = "cloudinary_public_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "cloudinary_public_id", length = 255)
    private String cloudinaryPublicId; // For Cloudinary integration

    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", length = 20)
    @Builder.Default
    private ImageType imageType = ImageType.GALLERY;

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "caption")
    private String caption;

    @Column(name = "file_size")
    private Long fileSize; // in bytes

    @Column(name = "width")
    private Integer width; // pixels

    @Column(name = "height")
    private Integer height; // pixels

    @Column(name = "alt_text")
    private String altText; // For SEO and accessibility

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl; // Optimized thumbnail

    // Helper Methods

    /**
     * Get file size in MB
     */
    public Double getFileSizeInMB() {
        return fileSize != null ? fileSize / (1024.0 * 1024.0) : null;
    }

    /**
     * Get aspect ratio
     */
    public Double getAspectRatio() {
        return (width != null && height != null && height != 0)
                ? (double) width / height
                : null;
    }

    /**
     * Check if image is landscape
     */
    public boolean isLandscape() {
        Double ratio = getAspectRatio();
        return ratio != null && ratio > 1.0;
    }

    /**
     * Check if image is portrait
     */
    public boolean isPortrait() {
        Double ratio = getAspectRatio();
        return ratio != null && ratio < 1.0;
    }

    /**
     * Check if image is square
     */
    public boolean isSquare() {
        Double ratio = getAspectRatio();
        return ratio != null && Math.abs(ratio - 1.0) < 0.01;
    }

    /**
     * Get display dimensions (max 800px)
     */
    public String getDisplayDimensions() {
        if (width == null || height == null)
            return null;

        int maxDimension = 800;
        if (width > maxDimension || height > maxDimension) {
            double scale = Math.min((double) maxDimension / width, (double) maxDimension / height);
            return String.format("%dx%d", (int) (width * scale), (int) (height * scale));
        }
        return String.format("%dx%d", width, height);
    }
}
