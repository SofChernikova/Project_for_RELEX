package vsu.ru.market.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vsu.ru.market.controllers.requests.RegisterRequest;
import vsu.ru.market.controllers.responses.AuthenticationResponse;
import vsu.ru.market.services.AuthenticationService;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity registration(@RequestBody RegisterRequest registerRequest ) throws NoSuchAlgorithmException {
        AuthenticationResponse authenticationResponse = service.register(registerRequest);
        if(authenticationResponse == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Отказано в регистрации!");
        return ResponseEntity.status(HttpStatus.OK).body(authenticationResponse);
    }

}
