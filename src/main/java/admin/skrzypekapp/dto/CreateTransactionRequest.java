package admin.skrzypekapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull(message = "Musisz wybrać konto")
        UUID accountId,

        @NotNull(message = "Kwota jest wymagana")
        @Positive(message = "Kwota musi być większa od zera")
        BigDecimal amount,

        @NotBlank(message = "Typ transakcji jest wymagany (INCOME/EXPENSE)")
        String type,

        @NotBlank(message = "Wybierz kategorię")
        String category,

        String description,

        @NotNull(message = "Data jest wymagana")
        LocalDate transactionDate
) {}