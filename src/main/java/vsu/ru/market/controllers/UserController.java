package vsu.ru.market.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import vsu.ru.market.models.User;
import vsu.ru.market.services.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/wallets")
    public ResponseEntity getAllWallets(Authentication authentication) {
        Map<String, String> result = userService.getAllWallets((User) authentication.getCredentials());
        if (result.size() == 1 && result.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/replenish")
    public ResponseEntity replenishBalance(Authentication authentication,
                                           @RequestBody Map<String, String> request) {
        Map<String, String> result = userService
                .replenishBalance((User) authentication.getCredentials(), request);

       if (result.containsKey("error")) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                   .body(result);
       }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/withdraw")
    public ResponseEntity withdrawMoney(Authentication authentication,
                                        @RequestBody Map<String, String> request) {
        Map<String, String> result = userService
                .withdrawMoney((User) authentication.getCredentials(), request);

        if (result.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PostMapping("/exchangeMoney")
    public ResponseEntity exchangeMoney(Authentication authentication,
                                        @RequestBody Map<String, String> request) {
        Map<String, String> result = userService
                .exchangeMoney((User) authentication.getCredentials(), request);

        if (result.size() == 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
