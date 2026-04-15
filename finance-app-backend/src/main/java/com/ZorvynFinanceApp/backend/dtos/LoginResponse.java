package com.ZorvynFinanceApp.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    
    private String username;

    private String token;

}
