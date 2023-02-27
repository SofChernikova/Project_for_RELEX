package vsu.ru.market.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vsu.ru.market.services.CommonService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/common")
@RequiredArgsConstructor
public class CommonController {
    private final CommonService commonService;

    @GetMapping("/exchangeRate")
    public ResponseEntity exchangeRate(@RequestBody Map<String, String> request) {
        Map<String, String> result = commonService.exchangeRate(request);

        if (result.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}

