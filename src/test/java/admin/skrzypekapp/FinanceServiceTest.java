package admin.skrzypekapp;

import admin.skrzypekapp.entity.Account;
import admin.skrzypekapp.entity.UserSettings;
import admin.skrzypekapp.repository.AccountRepository;
import admin.skrzypekapp.repository.UserSettingsRepository;
import admin.skrzypekapp.service.FinanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserSettingsRepository userSettingsRepository;


    @InjectMocks
    private FinanceService financeService;

    @Test
    void shouldCalculateTotalNetWorth() {
        // GIVEN (Przygotowanie danych)
        Long userId = 4L;

        Account acc1 = new Account();
        acc1.setBalance(new BigDecimal("15000.00"));

        Account acc2 = new Account();
        acc2.setBalance(new BigDecimal("5000.50"));

        when(accountRepository.findByUserId(userId)).thenReturn(List.of(acc1, acc2));

        // WHEN (Wykonanie akcji)
        BigDecimal netWorth = financeService.calculateNetWorth(userId);

        // THEN (Sprawdzenie wyniku)
        assertEquals(new BigDecimal("20000.50"), netWorth);
    }


    @Test
    void shouldCalculateFinancialRunway() {
        // GIVEN
        Long userId = 4L;

        Account acc = new Account();
        acc.setBalance(new BigDecimal("15000.00")); // Mamy 15k
        when(accountRepository.findByUserId(userId)).thenReturn(List.of(acc));

        UserSettings settings = new UserSettings();
        settings.setDefaultMonthlyExpenses(new BigDecimal("3000.00")); // Wydajemy 3k miesięcznie
        when(userSettingsRepository.findById(userId)).thenReturn(Optional.of(settings));

        // WHEN
        BigDecimal runway = financeService.calculateFinancialRunway(userId);

        // THEN
        // 15000 / 3000 = 5.00 miesięcy
        assertEquals(new BigDecimal("5.00"), runway.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void shouldCalculateEmergencyFundProgress() {
        // GIVEN
        Long userId = 4L;

        Account acc = new Account();
        acc.setBalance(new BigDecimal("18000.00")); // Mamy 18k
        when(accountRepository.findByUserId(userId)).thenReturn(List.of(acc));

        UserSettings settings = new UserSettings();
        settings.setDefaultMonthlyExpenses(new BigDecimal("3000.00"));
        settings.setEmergencyFundMonths(12); // Cel: 12 * 3000 = 36000
        when(userSettingsRepository.findById(userId)).thenReturn(Optional.of(settings));

        // WHEN
        BigDecimal progressPercentage = financeService.calculateEmergencyFundProgress(userId);

        // THEN
        // 18000 / 36000 = 0.5 -> 50.00%
        assertEquals(new BigDecimal("50.00"), progressPercentage.setScale(2, RoundingMode.HALF_UP));
    }
}