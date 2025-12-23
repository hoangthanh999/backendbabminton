package com.badminton.repository.system;

import com.badminton.entity.system.SystemSetting;
import com.badminton.enums.SettingCategory;
import com.badminton.enums.SettingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {

    // Basic Queries
    Optional<SystemSetting> findBySettingKey(String settingKey);

    boolean existsBySettingKey(String settingKey);

    // Category Queries
    List<SystemSetting> findByCategory(SettingCategory category);

    @Query("SELECT ss FROM SystemSetting ss WHERE ss.category = :category " +
            "ORDER BY ss.displayOrder, ss.name")
    List<SystemSetting> findByCategoryOrdered(@Param("category") SettingCategory category);

    // Type Queries
    List<SystemSetting> findBySettingType(SettingType settingType);

    // Public Settings
    @Query("SELECT ss FROM SystemSetting ss WHERE ss.isPublic = true " +
            "ORDER BY ss.category, ss.displayOrder")
    List<SystemSetting> findPublicSettings();

    @Query("SELECT ss FROM SystemSetting ss WHERE ss.category = :category " +
            "AND ss.isPublic = true " +
            "ORDER BY ss.displayOrder")
    List<SystemSetting> findPublicSettingsByCategory(@Param("category") SettingCategory category);

    // Editable Settings
    @Query("SELECT ss FROM SystemSetting ss WHERE ss.isEditable = true " +
            "ORDER BY ss.category, ss.displayOrder")
    List<SystemSetting> findEditableSettings();

    @Query("SELECT ss FROM SystemSetting ss WHERE ss.category = :category " +
            "AND ss.isEditable = true " +
            "ORDER BY ss.displayOrder")
    List<SystemSetting> findEditableSettingsByCategory(@Param("category") SettingCategory category);

    // Group Queries
    List<SystemSetting> findByGroupName(String groupName);

    @Query("SELECT ss FROM SystemSetting ss WHERE ss.groupName = :groupName " +
            "ORDER BY ss.displayOrder")
    List<SystemSetting> findByGroupNameOrdered(@Param("groupName") String groupName);

    @Query("SELECT DISTINCT ss.groupName FROM SystemSetting ss " +
            "WHERE ss.groupName IS NOT NULL " +
            "ORDER BY ss.groupName")
    List<String> findAllGroupNames();

    @Query("SELECT DISTINCT ss.groupName FROM SystemSetting ss " +
            "WHERE ss.category = :category " +
            "AND ss.groupName IS NOT NULL " +
            "ORDER BY ss.groupName")
    List<String> findGroupNamesByCategory(@Param("category") SettingCategory category);

    // All Settings Ordered
    @Query("SELECT ss FROM SystemSetting ss ORDER BY ss.category, ss.displayOrder, ss.name")
    List<SystemSetting> findAllOrdered();

    // Search
    @Query("SELECT ss FROM SystemSetting ss WHERE " +
            "LOWER(ss.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(ss.settingKey) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(ss.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SystemSetting> searchSettings(@Param("keyword") String keyword);

    // Statistics
    @Query("SELECT COUNT(ss) FROM SystemSetting ss WHERE ss.category = :category")
    long countByCategory(@Param("category") SettingCategory category);

    @Query("SELECT ss.category, COUNT(ss) FROM SystemSetting ss " +
            "GROUP BY ss.category " +
            "ORDER BY ss.category")
    List<Object[]> getCategoryStatistics();

    // Get Setting Value Methods (Helper queries)
    @Query("SELECT ss.settingValue FROM SystemSetting ss WHERE ss.settingKey = :key")
    Optional<String> findValueByKey(@Param("key") String key);

    @Query("SELECT COALESCE(ss.settingValue, ss.defaultValue) FROM SystemSetting ss " +
            "WHERE ss.settingKey = :key")
    Optional<String> findEffectiveValueByKey(@Param("key") String key);
}
