package com.badminton.entity.branch;

import com.badminton.entity.base.AuditableEntity;
import com.badminton.entity.court.Court;
import com.badminton.entity.user.User;
import com.badminton.enums.BranchStatus;
import com.badminton.enums.Region;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "branches", indexes = {
        @Index(name = "idx_branch_code", columnList = "code"),
        @Index(name = "idx_branch_region", columnList = "region"),
        @Index(name = "idx_branch_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE branches SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Branch extends AuditableEntity {

    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code; // HCM01, HN01, DN01

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false, length = 20)
    private Region region; // NORTH, CENTRAL, SOUTH

    // Contact Information
    @Column(name = "address", columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "ward", length = 100)
    private String ward;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    // Business Information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @Column(name = "opening_time")
    private LocalTime openingTime = LocalTime.of(6, 0);

    @Column(name = "closing_time")
    private LocalTime closingTime = LocalTime.of(22, 0);

    @Column(name = "timezone", length = 50)
    private String timezone = "Asia/Ho_Chi_Minh";

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BranchStatus status = BranchStatus.ACTIVE;

    @Column(name = "is_headquarters")
    private Boolean isHeadquarters = false;

    // Capacity
    @Column(name = "total_courts")
    private Integer totalCourts = 0;

    @Column(name = "total_staff")
    private Integer totalStaff = 0;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    // Financial
    @Column(name = "monthly_target", precision = 15, scale = 2)
    private BigDecimal monthlyTarget;

    // Metadata
    @Column(name = "settings", columnDefinition = "JSON")
    private String settings; // JSON string for flexible settings

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Relationships
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Court> courts = new HashSet<>();

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<BranchStaff> staff = new HashSet<>();

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<BranchInventory> inventory = new HashSet<>();

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<BranchRevenue> revenues = new HashSet<>();

    // Helper methods
    public void addCourt(Court court) {
        courts.add(court);
        court.setBranch(this);
        totalCourts = courts.size();
    }

    public void removeCourt(Court court) {
        courts.remove(court);
        court.setBranch(null);
        totalCourts = courts.size();
    }

    public void addStaff(BranchStaff branchStaff) {
        staff.add(branchStaff);
        branchStaff.setBranch(this);
        totalStaff = staff.size();
    }

    public boolean isOpen() {
        LocalTime now = LocalTime.now();
        return !now.isBefore(openingTime) && !now.isAfter(closingTime);
    }
}
