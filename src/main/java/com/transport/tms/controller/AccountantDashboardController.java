package com.transport.tms.controller;

import com.transport.tms.dto.response.AccountantDashboardResponse;
import com.transport.tms.service.AccountantDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accountant/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AccountantDashboardController {

    private final AccountantDashboardService accountantDashboardService;

    @GetMapping
    public ResponseEntity<AccountantDashboardResponse> getDashboard() {
        return ResponseEntity.ok(accountantDashboardService.getAccountantDashboard());
    }
}
