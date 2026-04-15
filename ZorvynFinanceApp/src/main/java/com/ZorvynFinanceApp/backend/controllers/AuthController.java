package com.ZorvynFinanceApp.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ZorvynFinanceApp.backend.dtos.AuthRequest;
import com.ZorvynFinanceApp.backend.dtos.LoginResponse;
import com.ZorvynFinanceApp.backend.helper.JwtUtil;
import com.ZorvynFinanceApp.backend.models.UserPrincipal;
import com.ZorvynFinanceApp.backend.services.MyUserDetailsService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthenticationManager authenticationManager;
    
    UserDetailsService userDetailsService;
    
    MyUserDetailsService myUserDetailsService;
    JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> generateToke (@RequestBody AuthRequest authRequest) {
        try{
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
            authenticationManager.authenticate(authToken);
            
        }catch (AuthenticationException authenticationException){
            throw new BadCredentialsException("Invalid username or password");
        }
        UserPrincipal user = (UserPrincipal)userDetailsService.loadUserByUsername(authRequest.getUsername());

        String token = jwtUtil.generateToken(user.getUsername());


        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(user.getUsername(), token));
    }
    @PostMapping("/signin")
    public ResponseEntity<?> createUser (@RequestBody AuthRequest authRequest) {
        myUserDetailsService.creatUser(authRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
}
