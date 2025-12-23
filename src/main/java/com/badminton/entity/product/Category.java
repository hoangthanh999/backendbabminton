package com.badminton.entity.product;

import com.badminton.entity.base.BaseEntity;
import com.badminton.enums.CategoryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(name = "uk_slug", columnNames = "slug"), indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_parent", columnList = "parent_id"),
        @Index(name = "idx_display_order", columnList = "display_order")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug", unique = true, length = 100)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CategoryStatus status = CategoryStatus.ACTIVE;

    // Hierarchy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Category> children = new HashSet<>();

    @Column(name = "level")
    @Builder.Default
    private Integer level = 0;

    @Column(name = "path")
    private String path; // /parent/child/grandchild

    // Display
    @Column(name = "icon")
    private String icon;

    @Column(name = "image")
    private String image;

    @Column(name = "banner")
    private String banner;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    // SEO
    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(name = "meta_keywords")
    private String metaKeywords;

    // Relationships
    @OneToMany(mappedBy = "category")
    @Builder.Default
    private Set<Product> products = new HashSet<>();

    // Lifecycle
    @PrePersist
    @PreUpdate
    public void updatePath() {
        if (parent != null) {
            this.level = parent.getLevel() + 1;
            this.path = parent.getPath() + "/" + slug;
        } else {
            this.level = 0;
            this.path = "/" + slug;
        }
    }

    // Helper Methods

    /**
     * Check if is root category
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Check if has children
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * Get all ancestors
     */
    public Set<Category> getAncestors() {
        Set<Category> ancestors = new HashSet<>();
        Category current = this.parent;

        while (current != null) {
            ancestors.add(current);
            current = current.getParent();
        }

        return ancestors;
    }

    /**
     * Get all descendants
     */
    public Set<Category> getDescendants() {
        Set<Category> descendants = new HashSet<>();

        for (Category child : children) {
            descendants.add(child);
            descendants.addAll(child.getDescendants());
        }

        return descendants;
    }

    /**
     * Add child category
     */
    public void addChild(Category child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * Remove child category
     */
    public void removeChild(Category child) {
        children.remove(child);
        child.setParent(null);
    }
}
