package admin.skrzypekapp.service;

import admin.skrzypekapp.dto.CreateAccountRequest;
import admin.skrzypekapp.entity.Account;
import admin.skrzypekapp.entity.AccountDefinitions;
import admin.skrzypekapp.entity.User;
import admin.skrzypekapp.repository.AccountDefinitionsRepository;
import admin.skrzypekapp.repository.AccountRepository;
import admin.skrzypekapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountDefinitionsRepository accountDefinitionsRepository;

    public List<Account> getUserAccounts(Long userId) {
        return accountRepository.findByUserId(userId);
    }
    public BigDecimal getBalance(Long userId) {
        return getUserAccounts(userId).stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public void createAccount(Long Id, CreateAccountRequest request) {
        User user = userRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AccountDefinitions definitions = new AccountDefinitions();
        definitions.setName(request.name());
        definitions.setIcon(request.icon());
        definitions.setGroupType(request.groupType());

        AccountDefinitions savedDefinition = accountDefinitionsRepository.save(definitions);

        Account account = new Account();
        account.setUser(user);
        account.setDefinition(savedDefinition);
        account.setBalance(request.initialBalance());
        account.setLastUpdate(LocalDateTime.now());

        accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(UUID accountId) {
        if(!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Account not found");
        }
        accountRepository.deleteById(accountId);
    }

    @Transactional
    public void updateBalance(UUID accountId, BigDecimal newBalance) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(newBalance);
        account.setLastUpdate(LocalDateTime.now());
        accountRepository.save(account);

    }

}
