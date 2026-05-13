package admin.skrzypekapp.service;

import admin.skrzypekapp.dto.CreateTransactionRequest;
import admin.skrzypekapp.dto.FinanceDashboardResponse;
import admin.skrzypekapp.dto.MonthlyStatDto;
import admin.skrzypekapp.entity.Account;
import admin.skrzypekapp.entity.Transaction;
import admin.skrzypekapp.entity.User;
import admin.skrzypekapp.entity.UserSettings;
import admin.skrzypekapp.repository.AccountRepository;
import admin.skrzypekapp.repository.TransactionRepository;
import admin.skrzypekapp.repository.UserRepository;
import admin.skrzypekapp.repository.UserSettingsRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;


@Service
@RequiredArgsConstructor
public class FinanceService {

    private final AccountRepository accountRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public BigDecimal calculateNetWorth(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);

        return accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void addTransaction(Long userId, UUID accountId, BigDecimal amount, String type, String category, String description, LocalDate date){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setDescription(description);
        transaction.setTransactionDate(date);
        transactionRepository.save(transaction);

        BigDecimal newBalance;
        if("INCOME".equalsIgnoreCase(type)){
            newBalance = account.getBalance().add(amount);
        }
        else{
            newBalance = account.getBalance().subtract(amount);
        }
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    public List<MonthlyStatDto> getMonthlyStats(Long userId, int year) {
        List<Object[]> result = transactionRepository.findMonthlyStats(userId, year);

        Map<Integer, MonthlyStatDto> statsMap = new HashMap<>();
        for(int i = 1; i < 12; i++){
            statsMap.put(i, new MonthlyStatDto(i, BigDecimal.ZERO, BigDecimal.ZERO));
        }
        for (Object[] row : result) {
            Integer month = ((Number) row[0]).intValue();

            BigDecimal income = new BigDecimal(row[1].toString());
            BigDecimal expense = new BigDecimal(row[2].toString());

            statsMap.put(month, new MonthlyStatDto(month, income, expense));
        }
        return statsMap.values().stream()
                .sorted(Comparator.comparing(MonthlyStatDto::getMonth))
                .toList();
    }


    public BigDecimal calculateFinancialRunway(Long userId) {
        BigDecimal netWorth = calculateNetWorth(userId);

        UserSettings settings = userSettingsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Settings not found"));

        if(settings.getDefaultMonthlyExpenses().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return netWorth.divide(settings.getDefaultMonthlyExpenses(), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateEmergencyFundProgress(Long userId) {
        BigDecimal netWorth = calculateNetWorth(userId);
        UserSettings settings = userSettingsRepository.findById(userId)
                .orElseThrow((() -> new RuntimeException("Settings not found")));

        BigDecimal targetAmount = settings.getDefaultMonthlyExpenses()
                .multiply(BigDecimal.valueOf(settings.getEmergencyFundMonths()));

        if(targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return netWorth.divide(targetAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

    }
    public FinanceDashboardResponse getDashboardData(Long userId) {

        Pageable topFive = PageRequest.of(0, 5);

        List<Transaction> recentEntities = transactionRepository.findRecentTransactions(userId, topFive);

        List<FinanceDashboardResponse.TransactionDto> recentDtos = recentEntities.stream()
                .map(t -> FinanceDashboardResponse.TransactionDto.builder()
                        .id(t.getId())
                        .amount(t.getAmount())
                        .type(t.getType())
                        .category(t.getCategory())
                        .description(t.getDescription())
                        .transactionDate(t.getTransactionDate())
                        .build())
                .toList();

        return FinanceDashboardResponse.builder()
                .totalNetWorth(calculateNetWorth(userId))
                .financialRunwayMonths(calculateFinancialRunway(userId))
                .emergencyFundProgressPercent(calculateEmergencyFundProgress(userId))
                .recentTransactions(recentDtos)
                .build();
    }

    @Transactional
    public void deleteTransaction(UUID id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transakcja nie istnieje"));

        Account account = tx.getAccount();

        if ("EXPENSE".equals(tx.getType())) {
            account.setBalance(account.getBalance().add(tx.getAmount()));
        } else if ("INCOME".equals(tx.getType())) {
            account.setBalance(account.getBalance().subtract(tx.getAmount()));
        }

        accountRepository.save(account);
        transactionRepository.delete(tx);
    }


    @Transactional
    public void updateTransaction(UUID id, CreateTransactionRequest request) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono transakcji"));

        if (!tx.getAccount().getId().equals(request.accountId())) {
            Account newAccount = accountRepository.findById(request.accountId())
                    .orElseThrow(() -> new RuntimeException("Wybrane konto nie istnieje"));
            tx.setAccount(newAccount);
        }
        tx.setAmount(request.amount());
        tx.setCategory(request.category() != null ? request.category() : "Inne");
        tx.setDescription(request.description());
        tx.setTransactionDate(request.transactionDate());
        tx.setType(request.type());

        transactionRepository.save(tx);
    }
}
