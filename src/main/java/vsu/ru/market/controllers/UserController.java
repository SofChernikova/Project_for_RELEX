package vsu.ru.market.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vsu.ru.market.controllers.requests.AllWalletsRequest;
import vsu.ru.market.services.UserService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/wallets")
    public ResponseEntity getAllWallets(@RequestBody AllWalletsRequest allWalletsRequest){
        Map<String, String> wallets = userService.getAllWallets(allWalletsRequest);
        if(wallets.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Нет активных кошельков!");
        else if(wallets.size() == 1 && wallets.containsKey("error")) return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(wallets);
        return  ResponseEntity.status(HttpStatus.OK).body(wallets);
    }

    @PostMapping("/replenish")
    public ResponseEntity replenishBalance(@RequestBody Map<String, String> request){
        Map<String, String> wallets = userService.replenishBalance(request);
        if(wallets.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Нет запрашиваемого кошелька!");
        else if(wallets.size() == 1 && wallets.containsKey("error")) return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(wallets);
        return  ResponseEntity.status(HttpStatus.OK).body(wallets);
    }

    @PostMapping("/withdraw")
    public ResponseEntity withdrawMoney(@RequestBody Map<String, String> request){
        Map<String, String> wallets = userService.withdrawMoney(request);
        if(wallets.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Нет запрашиваемого кошелька!");
        else if(wallets.size() == 1 && wallets.containsKey("error")) return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(wallets);
        return  ResponseEntity.status(HttpStatus.OK).body(wallets);
    }

    @GetMapping("/exchangeRate")
    public ResponseEntity exchangeRate(@RequestBody Map<String, String> request){
        Map<String, String> rates = userService.exchangeRate(request);
        if(rates.isEmpty()) return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Откзано в выполнении!");
        return  ResponseEntity.status(HttpStatus.OK).body(rates);
    }

    @PostMapping("/exchangeMoney")
    public ResponseEntity exchangeMoney(@RequestBody Map<String, String> request){
        Map<String, String> result = userService.exchangeMoney(request);
       if(result.size() == 1 && result.containsKey("error")) return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(result);
        return  ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
