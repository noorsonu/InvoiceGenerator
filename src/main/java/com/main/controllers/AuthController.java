package com.main.controllers;

import com.main.dtos.AdminDto;
import com.main.dtos.AuthResponse;
import com.main.dtos.ErrorResponse;
import com.main.dtos.LoginRequest;
import com.main.dtos.RegisterRequest;
import com.main.security.JwtHelper;
import com.main.security.TokenBlackList;
import com.main.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Auth", description = "Admin registration, login and logout endpoints")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private TokenBlackList tokenBlacklist;

    @Autowired
    private AdminService adminService;

    @Operation(summary = "Register a new admin")
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerAdmin(@RequestBody  RegisterRequest req, HttpServletRequest httpReq) {
        try {
            AdminDto toCreate = new AdminDto();
            toCreate.setName(req.getName());
            toCreate.setEmail(req.getEmail());
            toCreate.setPhoneNumber(req.getPhoneNumber());
            toCreate.setPassword(req.getPassword());
            AdminDto created = adminService.registerAdmin(toCreate);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ErrorResponse.of(HttpStatus.CONFLICT, ex.getMessage(), httpReq.getRequestURI()));
        }
    }

    @Operation(summary = "Login and obtain access token")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest req, HttpServletRequest httpReq) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
            UserDetails user = (UserDetails) auth.getPrincipal();
            String access = jwtHelper.generateAccessToken(user);
            return ResponseEntity.ok(new AuthResponse("Bearer", access));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Invalid email or password", httpReq.getRequestURI()));
        }
    }

    @Operation(summary = "Logout by blacklisting current access token")
    @PostMapping(value = "/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String header, HttpServletRequest httpReq) {
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            tokenBlacklist.blacklist(token);
            return ResponseEntity.ok(Map.of("message", "Logout successful"));
        }
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, "No token provided", httpReq.getRequestURI()));
    }
}
