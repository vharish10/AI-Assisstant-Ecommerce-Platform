package com.Revature.Ecommerce.Platform.controller;

import com.Revature.Ecommerce.Platform.dto.AuthRequest;
import com.Revature.Ecommerce.Platform.models.User;
import com.Revature.Ecommerce.Platform.security.JwtUtil;
import com.Revature.Ecommerce.Platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {

        User user = userService.login(request.getEmail(), request.getPassword());

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return ResponseEntity.ok(token);
    }

    @GetMapping("/admin-test")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminTest() {
        return "Admin access granted";
    }

    @GetMapping("/user-test")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String userTest() {
        return "User access granted";
    }
}