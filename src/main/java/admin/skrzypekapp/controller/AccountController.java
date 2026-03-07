package admin.skrzypekapp.controller;

import admin.skrzypekapp.entity.User;
import admin.skrzypekapp.repository.UserRepository;
import admin.skrzypekapp.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getMyAccount(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Nie ma Uzytkownika"));

        Long id = user.getId();
        Map<String, Object> response = new HashMap<>();

        response.put("accounts", accountService.getUserAccounts(id));
        response.put("totalBalance", accountService.getBalance(id));

        return ResponseEntity.ok(response);
    }
}
