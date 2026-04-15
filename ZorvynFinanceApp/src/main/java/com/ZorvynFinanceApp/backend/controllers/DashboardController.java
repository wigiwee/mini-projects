package com.ZorvynFinanceApp.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;

import com.ZorvynFinanceApp.backend.dtos.DashboardDto;
import com.ZorvynFinanceApp.backend.models.UserPrincipal;
import com.ZorvynFinanceApp.backend.services.DashboardService;


@Controller
@RestController
@CrossOrigin
@RequestMapping("/api/v1/dashboard")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardController {

    DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDto> getDashboard() {
        UserPrincipal userPrincipal =  (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getDashboard(userPrincipal.getUser()));
    }
}
