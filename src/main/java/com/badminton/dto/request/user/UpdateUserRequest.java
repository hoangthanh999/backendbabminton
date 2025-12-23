package com.badminton.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

/**
 * Update user request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(min = 2, max = 100, message = "Họ tên phải từ 2-100 ký tự")
    private String name;

    @Email(message = "Email không hợp lệ")
    private String email;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    private String city;

    private String district;

    private String ward;

    private Long branchId;

    private Set<Long> roleIds;

    private String status;

    private String notes;
}
