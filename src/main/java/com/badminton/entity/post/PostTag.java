package com.badminton.entity.post;

import com.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "post_tags", uniqueConstraints = @UniqueConstraint(name = "uk_slug", columnNames = "slug"), indexes = {
        @Index(name = "idx_slug", columnList = "slug"),
        @Index(name = "idx_usage_count", columnList = "usage_count")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostTag extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "slug", unique = true, nullable = false, length = 100)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "color", length = 7)
    private String color; // Hex color

    @Column(name = "usage_count")
    @Builder.Default
    private Long usageCount = 0L;

    @ManyToMany(mappedBy = "postTags")
    @Builder.Default
    private Set<Post> posts = new HashSet<>();

    // Helper Methods

    /**
     * Increment usage count
     */
    public void incrementUsageCount() {
        this.usageCount++;
    }

    /**
     * Decrement usage count
     */
    public void decrementUsageCount() {
        if (this.usageCount > 0) {
            this.usageCount--;
        }
    }
}
