package admin.skrzypekapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column (nullable = false, name = "default_monthly_expenses")
    private BigDecimal defaultMonthlyExpenses;

    @Column (nullable = false, name = "emergency_fund_months")
    private int emergencyFundMonths;
}
