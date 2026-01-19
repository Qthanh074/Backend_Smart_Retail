package com.smartretail.backend.controller;

import com.smartretail.backend.dto.request.LoginRequest;
import com.smartretail.backend.dto.request.RegisterRequest;
import com.smartretail.backend.dto.response.ApiResponse;
import com.smartretail.backend.dto.response.LoginResponse;
import com.smartretail.backend.security.JwtTokenProvider;
import com.smartretail.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đăng nhập thành công", new LoginResponse(jwt, "Bearer")));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        // Trả về ApiResponse thay vì String thuần túy
        return ResponseEntity.ok(new ApiResponse<>(true, "Đăng ký tài khoản thành công!", null));
    }
}