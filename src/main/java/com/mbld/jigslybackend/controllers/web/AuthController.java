package com.mbld.jigslybackend.controllers.web;

import com.mbld.jigslybackend.entities.dto.AuthResponse;
import com.mbld.jigslybackend.entities.dto.LoginRequest;
import com.mbld.jigslybackend.entities.dto.RegisterRequest;
import com.mbld.jigslybackend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
