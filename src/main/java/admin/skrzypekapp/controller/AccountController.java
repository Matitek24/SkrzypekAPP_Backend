package admin.skrzypekapp.controller;

import admin.skrzypekapp.dto.CreateAccountRequest;
import admin.skrzypekapp.entity.User;
import admin.skrzypekapp.repository.UserRepository;
import admin.skrzypekapp.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    @PostMapping
    public ResponseEntity<Void> createAcount(@Valid @RequestBody CreateAccountRequest accountRequest) {
        Long userId = 4L;

        accountService.createAccount(userId, accountRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/balance")
    public ResponseEntity<Void> updateBalance(
            @PathVariable UUID id,
            @RequestBody Map<String, BigDecimal> updates
    ){
        BigDecimal newBalance = updates.get("balance");
        accountService.updateBalance(id, newBalance);
        return ResponseEntity.ok().build();
    }
}
