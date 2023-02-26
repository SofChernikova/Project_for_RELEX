package vsu.ru.market.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import vsu.ru.market.controllers.responses.AuthenticationResponse;
import vsu.ru.market.controllers.requests.RegisterRequest;
import vsu.ru.market.models.Role;
import vsu.ru.market.models.User;
import vsu.ru.market.repo.RoleRepository;
import vsu.ru.market.repo.UserRepository;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;

    private String generateSecretKey(RegisterRequest registerRequest) throws NoSuchAlgorithmException {
        String input = registerRequest.getUsername() + registerRequest.getEmail();
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        String key = DatatypeConverter.printHexBinary(digest);
        return key;
    }

    public AuthenticationResponse register(RegisterRequest registerRequest) throws NoSuchAlgorithmException {
        var tempUser = repository.findByUsername(registerRequest.getUsername());
        var tempUser1 = repository.findByEmail(registerRequest.getEmail());

        if (tempUser.isEmpty() && tempUser1.isEmpty()) {
            String key = generateSecretKey(registerRequest);
            Role role = roleRepository.findById(2).orElseThrow();
            Set<Role> set = new HashSet<>();
            set.add(role);
            var user = User.builder()
                    .username(registerRequest.getUsername())
                    .email(registerRequest.getEmail())
                    .secretKey(key)
                    .roles(set)
                    .build();
            repository.save(user);
            var response = AuthenticationResponse.builder()
                    .secretKey(key)
                    .build();
            return response;
        }
        return null;
    }

}
