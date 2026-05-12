package admin.skrzypekapp.dto;

import admin.skrzypekapp.entity.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
        private String id;
        private BigDecimal amount;
        private String type;
        private String category;
        private String description;
        private LocalDate transactionDate;
    }
}
