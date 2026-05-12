package admin.skrzypekapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class MonthlyStatDto {
    private int month;
    private BigDecimal income;
    private BigDecimal expense;

}
