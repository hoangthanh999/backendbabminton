package com.badminton.entity.branch;

import com.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "branch_revenue", uniqueConstraints = @UniqueConstraint(name = "uk_branch_date", columnNames = {
        "branch_id", "date" }), indexes = {
                @Index(name = "idx_date", columnList = "date"),
                @Index(name = "idx_revenue", columnList = "branch_id, total_revenue")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchRevenue extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    // Revenue Breakdown
    @Column(name = "booking_revenue", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal bookingRevenue = BigDecimal.ZERO;

    @Column(name = "product_revenue", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal productRevenue = BigDecimal.ZERO;

    @Column(name = "service_revenue", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal serviceRevenue = BigDecimal.ZERO;

    @Column(name = "other_revenue", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal otherRevenue = BigDecimal.ZERO;

    @Column(name = "total_revenue", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    // Expenses
    @Column(name = "total_expenses", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalExpenses = BigDecimal.ZERO;

    @Column(name = "net_profit", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal netProfit = BigDecimal.ZERO;

    // Transactions
    @Column(name = "total_bookings")
    @Builder.Default
    private Integer totalBookings = 0;

    @Column(name = "total_orders")
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(name = "total_customers")
    @Builder.Default
    private Integer totalCustomers = 0;

    // Helper methods
    public void calculateTotalRevenue() {
        this.totalRevenue = bookingRevenue
                .add(productRevenue)
                .add(serviceRevenue)
                .add(otherRevenue);
    }

    public void calculateNetProfit() {
        this.netProfit = totalRevenue.subtract(totalExpenses);
    }

    public void addBookingRevenue(BigDecimal amount) {
        this.bookingRevenue = this.bookingRevenue.add(amount);
        this.totalBookings++;
        calculateTotalRevenue();
        calculateNetProfit();
    }

    public void addProductRevenue(BigDecimal amount) {
        this.productRevenue = this.productRevenue.add(amount);
        this.totalOrders++;
        calculateTotalRevenue();
        calculateNetProfit();
    }
}
