package com.supreme.controllers;

import com.supreme.entity.User;
import com.supreme.payload.request.LoginRequest;
import com.supreme.payload.response.ErrorResponse;
import com.supreme.payload.response.JwtResponse;
import com.supreme.payload.response.Response;
import com.supreme.repository.UserRepository;
import com.supreme.security.jwt.JwtUtils;
import com.supreme.serviceImpl.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByMobileNumber(loginRequest.getMobileNumber());
        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "MSG14", "User with mobile number doesn't exists"));
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getMobileNumber(), loginRequest.getPin()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        JwtResponse jwtResponse = new JwtResponse(userDetails.getId(),
                userDetails.getUsername(),
                roles.get(0),
                jwt);
        return new ResponseEntity<>(new Response(HttpStatus.OK.value(), 1, "Login successful", "MSG1", jwtResponse), HttpStatus.OK);
    }

}
