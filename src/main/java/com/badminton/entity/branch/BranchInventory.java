package com.badminton.entity.branch;

import com.badminton.entity.base.BaseEntity;
import com.badminton.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "branch_inventory", uniqueConstraints = @UniqueConstraint(name = "uk_branch_product", columnNames = {
        "branch_id", "product_id" }), indexes = {
                @Index(name = "idx_low_stock", columnList = "branch_id, quantity")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchInventory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Column(name = "min_stock")
    @Builder.Default
    private Integer minStock = 10;

    @Column(name = "max_stock")
    @Builder.Default
    private Integer maxStock = 100;

    @Column(name = "reorder_point")
    @Builder.Default
    private Integer reorderPoint = 20;

    @Column(name = "last_restock_date")
    private LocalDate lastRestockDate;

    @Column(name = "location", length = 100)
    private String location; // Vị trí trong kho

    // Helper methods
    public boolean isLowStock() {
        return quantity <= minStock;
    }

    public boolean needsReorder() {
        return quantity <= reorderPoint;
    }

    public boolean isOutOfStock() {
        return quantity == 0;
    }

    public void addStock(Integer amount) {
        this.quantity += amount;
        this.lastRestockDate = LocalDate.now();
    }

    public void removeStock(Integer amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
        } else {
            throw new IllegalArgumentException("Insufficient stock");
        }
    }
}
