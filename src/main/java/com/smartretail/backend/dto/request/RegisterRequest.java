package com.smartretail.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Mật khẩu phải chứa ít nhất 1 chữ hoa và 1 ký tự đặc biệt")
    private String password;
}