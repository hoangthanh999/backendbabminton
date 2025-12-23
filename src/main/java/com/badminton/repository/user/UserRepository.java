package com.badminton.repository.user;

import com.badminton.entity.user.User;
import com.badminton.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // Basic Queries
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    // Status Queries
    List<User> findByStatus(UserStatus status);

    Page<User> findByStatus(UserStatus status, Pageable pageable);

    List<User> findByStatusAndDeletedAtIsNull(UserStatus status);

    // Role Queries
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.status = :status")
    List<User> findByRoleNameAndStatus(@Param("roleName") String roleName, @Param("status") UserStatus status);

    // Search Queries
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "u.phone LIKE CONCAT('%', :keyword, '%')")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    // Date Range Queries
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<User> findByLastLoginBetween(LocalDateTime start, LocalDateTime end);

    // Statistics
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countByStatus(@Param("status") UserStatus status);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :date")
    long countNewUsersAfter(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLogin >= :date")
    long countActiveUsersAfter(@Param("date") LocalDateTime date);

    // Email Verification
    Optional<User> findByEmailVerificationToken(String token);

    List<User> findByEmailVerifiedFalseAndCreatedAtBefore(LocalDateTime date);

    // Password Reset
    Optional<User> findByPasswordResetToken(String token);

    List<User> findByPasswordResetTokenExpiryBefore(LocalDateTime date);

    // Branch Queries
    @Query("SELECT u FROM User u WHERE u.branch.id = :branchId")
    List<User> findByBranchId(@Param("branchId") Long branchId);

    @Query("SELECT u FROM User u WHERE u.branch.id = :branchId AND u.status = :status")
    List<User> findByBranchIdAndStatus(@Param("branchId") Long branchId, @Param("status") UserStatus status);

    // Custom Queries
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL ORDER BY u.createdAt DESC")
    List<User> findRecentUsers(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.loyaltyPoints > :minPoints ORDER BY u.loyaltyPoints DESC")
    List<User> findTopLoyaltyUsers(@Param("minPoints") Integer minPoints, Pageable pageable);
}
