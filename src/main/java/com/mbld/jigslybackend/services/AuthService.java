package com.mbld.jigslybackend.services;

import com.mbld.jigslybackend.entities.Role;
import com.mbld.jigslybackend.entities.User;
import com.mbld.jigslybackend.entities.dto.AuthResponse;
import com.mbld.jigslybackend.entities.dto.LoginRequest;
import com.mbld.jigslybackend.entities.dto.RegisterRequest;
import com.mbld.jigslybackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    public AuthResponse register(RegisterRequest request) {
        var user = User
                .builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(savedUser);

        return AuthResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        var user = userRepository.findByUsername(
                request.username()).orElseThrow(() -> new UsernameNotFoundException("User not found by username: "+request.username()));
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse
                .builder()
                .token(jwtToken)
                .build();
    }
}
