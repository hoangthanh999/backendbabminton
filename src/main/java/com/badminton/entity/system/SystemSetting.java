package com.badminton.entity.system;

import com.badminton.entity.base.BaseEntity;
import com.badminton.enums.SettingCategory;
import com.badminton.enums.SettingType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_settings", uniqueConstraints = @UniqueConstraint(name = "uk_key", columnNames = "setting_key"), indexes = {
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_key", columnList = "setting_key")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSetting extends BaseEntity {

    @Column(name = "setting_key", nullable = false, unique = true, length = 100)
    private String settingKey;

    @Column(name = "setting_value", columnDefinition = "TEXT")
    private String settingValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "setting_type", nullable = false, length = 20)
    private SettingType settingType; // STRING, INTEGER, BOOLEAN, JSON, etc.

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private SettingCategory category;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "default_value", columnDefinition = "TEXT")
    private String defaultValue;

    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = false;

    @Column(name = "is_editable")
    @Builder.Default
    private Boolean isEditable = true;

    @Column(name = "validation_rule")
    private String validationRule;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "group_name", length = 100)
    private String groupName;

    // Helper Methods

    /**
     * Get value as string
     */
    public String getValueAsString() {
        return settingValue != null ? settingValue : defaultValue;
    }

    /**
     * Get value as integer
     */
    public Integer getValueAsInteger() {
        String value = getValueAsString();
        try {
            return value != null ? Integer.parseInt(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Get value as boolean
     */
    public Boolean getValueAsBoolean() {
        String value = getValueAsString();
        return value != null ? Boolean.parseBoolean(value) : null;
    }

    /**
     * Get value as double
     */
    public Double getValueAsDouble() {
        String value = getValueAsString();
        try {
            return value != null ? Double.parseDouble(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Set value from object
     */
    public void setValue(Object value) {
        if (value == null) {
            this.settingValue = null;
        } else {
            this.settingValue = value.toString();
        }
    }

    /**
     * Reset to default
     */
    public void resetToDefault() {
        this.settingValue = defaultValue;
    }

    /**
     * Validate value
     */
    public boolean isValidValue(String value) {
        if (validationRule == null || validationRule.isEmpty()) {
            return true;
        }

        // Simple validation based on type
        switch (settingType) {
            case INTEGER:
                try {
                    Integer.parseInt(value);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case BOOLEAN:
                return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
            case DECIMAL:
                try {
                    Double.parseDouble(value);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            default:
                return true;
        }
    }
}
