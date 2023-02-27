package vsu.ru.market.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vsu.ru.market.services.AdminService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    @PostMapping("/changeExchangeRate")
    public ResponseEntity changeExchangeRate(@RequestBody Map<String, String> request) {
        Map<String, String> result = adminService.changeExchangeRate(request);

        if (result.size() == 1 && result.containsKey("error")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/totalAmount")
    public ResponseEntity totalAmount(@RequestBody Map<String, String> request) {
        Map<String, String> result = adminService.totalAmount(request);

        if (result.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/totalTransactions")
    public ResponseEntity totalTransactions(@RequestBody Map<String, String> request) {
        Map<String, String> result = adminService.totalTransactions(request);

        if (result.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}

