package com.stockpulse.stockpulseAPI.global.test;

import com.stockpulse.stockpulseAPI.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/test")
public class Controller {

    @Operation(summary = "Health check API", description = "Server Health check")
    @GetMapping("health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.onSuccess("성공");
    }
}
