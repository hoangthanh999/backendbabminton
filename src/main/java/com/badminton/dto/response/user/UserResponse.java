package com.badminton.dto.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * User response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String city;
    private String district;
    private String ward;
    private String status;

    private BranchInfo branch;
    private Set<RoleInfo> roles;

    private LocalDateTime lastLogin;
    private String lastLoginIp;
    private Integer loginAttempts;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
    public static class RoleInfo {
        private Long id;
        private String name;
        private String displayName;
    }
}
