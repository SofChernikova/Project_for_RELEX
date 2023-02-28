package vsu.ru.market.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import org.springframework.transaction.annotation.Transactional;
import vsu.ru.market.models.Role;
import vsu.ru.market.models.User;

import vsu.ru.market.repo.RoleRepository;
import vsu.ru.market.repo.UserRepository;
import vsu.ru.market.services.optional.AdditionalService;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final UserRepository repository;
    private final RoleRepository roleRepository;

    private String generateSecretKey(String username, String email) {
        try {
            String input = username + email;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            String key = DatatypeConverter.printHexBinary(digest);
            return key;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }


    @Transactional
    public Map<String, String> register(Map<String, String> request) {
        Map<String, String> result = new HashMap<>();

        String[] requiredParam = {"username", "email"};
        if (!AdditionalService.areParametersValid(request, requiredParam, 1)) {
            result.put("error", "Нет необходимого параметра!");
            return result;
        }
        String username = request.get("username");
        String email = request.get("email");

        var byUsername = repository.findByUsername(username);
        var byEmail = repository.findByEmail(email);

        if (!byUsername.isEmpty()) {
            result.put("error", "Не уникальное имя пользователя!");
            return result;
        }
        if (!byEmail.isEmpty()) {
            result.put("error", "Не уникальная почта!");
            return result;
        }

        String key = generateSecretKey(username, email);
        if (key == null) {
            result.put("error", "Не удалось создать ключ");
            return result;
        }

        Optional<Role> role = roleRepository.findById(2);
        if (role.isEmpty()) {
            result.put("error", "Не удалось получить роль!");
            return result;
        }

        Set<Role> set = new HashSet<>();
        set.add(role.get());
        var user = User.builder()
                .username(username)
                .email(email)
                .secretKey(key)
                .roles(set)
                .build();
        repository.save(user);

        result.put("key", key);
        return result;
    }
}
