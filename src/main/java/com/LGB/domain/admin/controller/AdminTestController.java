package com.LGB.domain.admin.controller;

import com.LGB.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminTestController {

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Void>> test() {
        return ResponseEntity.ok(ApiResponse.successMessage("ADMIN_OK"));
    }
}
