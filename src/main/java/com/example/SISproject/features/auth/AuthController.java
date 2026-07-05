package com.example.SISproject.features.auth;

import com.example.SISproject.features.auth.dto.AuthResponse;
import com.example.SISproject.features.auth.dto.LoginRequest;
import com.example.SISproject.features.auth.dto.MeRequest;
import com.example.SISproject.features.auth.dto.RegisterRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        if (token == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        createNewCookie(response,token);
        return ResponseEntity.ok(new AuthResponse("Logged-in Successfully!"));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest, HttpServletResponse response){
        String token = authService.register(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());
        if (token == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        createNewCookie(response,token);
        return ResponseEntity.ok(new AuthResponse("Registered Successfully!"));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(new AuthResponse("Welcome back " + userDetails.getUsername()));
    }

    private void createNewCookie(HttpServletResponse response,String token){
        Cookie jwtCookie = new Cookie("jwt",token);
        jwtCookie.setHttpOnly(true);   // Protects against XSS
        jwtCookie.setSecure(false);    // Set to true in production (HTTPS)
        jwtCookie.setPath("/");        // Accessible globally across your API routes
        jwtCookie.setMaxAge(3600/4);
        jwtCookie.setAttribute("SameSite", "Strict");
        response.addCookie(jwtCookie);
    }

}
