package com.example.ElearningApp.controllers;

import com.example.ElearningApp.DTO.AuthRequest;
import com.example.ElearningApp.DTO.AuthResponse;
import com.example.ElearningApp.entity.User;
import com.example.ElearningApp.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Data
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // cho phép Flutter gọi API
public class AuthController {

    @Autowired
    private UserService userService;

    // ✅ Register
    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest request) {
        if (userService.findByUsername(request.getUsername()).isPresent()) {
            return new AuthResponse(false, "❌ Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // 🚨 TODO: mã hoá BCrypt trong production
        userService.save(user);

        return new AuthResponse(true, "✅ Register success");
    }

    // ✅ Login
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return userService.findByUsername(request.getUsername())
                .map(user -> {
                    if (user.getPassword().equals(request.getPassword())) {
                        return new AuthResponse(true, "✅ Login success");
                    } else {
                        return new AuthResponse(false, "❌ Invalid password");
                    }
                })
                .orElse(new AuthResponse(false, "❌ User not found"));
    }
}
