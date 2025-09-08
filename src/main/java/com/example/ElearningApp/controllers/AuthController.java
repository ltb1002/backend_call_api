package com.example.ElearningApp.controllers;

import com.example.ElearningApp.DTO.AuthResponse;
import com.example.ElearningApp.entity.User;
import com.example.ElearningApp.service.PasswordResetService;
import com.example.ElearningApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private UserService userService;
    private PasswordResetService passwordResetService;
    private PasswordEncoder passwordEncoder;

    // Constructor thủ công để Spring inject
    public AuthController(UserService userService,
                          PasswordResetService passwordResetService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------------- REGISTER ----------------
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || email.isBlank() || password == null || password.isBlank())
            return ResponseEntity.badRequest().body(new AuthResponse(false, "Email & password cannot be empty"));

        if (userService.findByEmail(email).isPresent())
            return ResponseEntity.badRequest().body(new AuthResponse(false, "Email already exists"));

        User user = new User(email, passwordEncoder.encode(password));
        userService.save(user);

        return ResponseEntity.ok(new AuthResponse(true, "User registered successfully"));
    }

    // ---------------- LOGIN ----------------
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(password, user.getPassword()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Invalid password"));

        return ResponseEntity.ok(new AuthResponse(true, "Login successful"));
    }

    // ---------------- FORGOT PASSWORD ----------------
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isBlank() || userService.findByEmail(email).isEmpty())
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email not found"));

        String token = passwordResetService.createPasswordResetToken(email);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Reset token generated",
                "token", token
        ));
    }

    // ---------------- RESET PASSWORD ----------------
    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        try {
            boolean success = passwordResetService.resetPassword(token, newPassword);
            if (success) return ResponseEntity.ok(new AuthResponse(true, "Password reset successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, e.getMessage()));
        }

        return ResponseEntity.badRequest().body(new AuthResponse(false, "Invalid token or email"));
    }
}
