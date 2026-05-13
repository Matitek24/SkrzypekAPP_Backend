package admin.skrzypekapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank(message = "Nazwa konta nie może być pusta")
        String name,

        @NotNull(message = "Saldo początkowe jest wymagane")
        @PositiveOrZero(message = "Saldo nie może być ujemne")
        BigDecimal initialBalance,

        @NotBlank(message = "Ikona jest wymagana")
        String icon,

        @NotBlank(message = "Typ grupy jest wymagany")
        String groupType

) {
}
