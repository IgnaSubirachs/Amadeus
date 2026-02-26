package com.amadeus.web.controller;

import com.amadeus.domain.model.User;
import com.amadeus.domain.service.UserService;
import com.amadeus.infrastructure.security.CustomUserDetails;
import com.amadeus.infrastructure.security.JwtService;
import com.amadeus.web.dto.AuthRequest;
import com.amadeus.web.dto.AuthResponse;
import com.amadeus.web.dto.CreateUserRequest;
import com.amadeus.web.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        UserDetails userDetails = new CustomUserDetails(user);

        String jwtToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwtToken, UserMapper.toDTO(user)));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // Esto lanzará excepción si las credenciales son incorrectas
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(userDetails);

        // Obtener usuario completo
        User user = userService.findByEmail(request.email()).orElseThrow();

        return ResponseEntity.ok(new AuthResponse(jwtToken, UserMapper.toDTO(user)));
    }
}
