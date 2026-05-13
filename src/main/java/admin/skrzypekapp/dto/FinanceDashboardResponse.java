package admin.skrzypekapp.dto;

import admin.skrzypekapp.entity.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class FinanceDashboardResponse {
    private BigDecimal totalNetWorth;
    private BigDecimal financialRunwayMonths;
    private BigDecimal emergencyFundProgressPercent;
    private List<TransactionDto> recentTransactions;

    @Data
    @Builder
    public static class TransactionDto {
        private UUID id;
        private BigDecimal amount;
        private String type;
        private String category;
        private String description;
        private LocalDate transactionDate;
    }
}
