package admin.skrzypekapp.repository;

import admin.skrzypekapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUserId(Long userId);

    @Query(value = "SELECT EXTRACT(MONTH FROM transaction_date) as month, " +
            "SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) as income, " +
            "SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) as expense " +
            "FROM transactions WHERE user_id = :userId AND EXTRACT(YEAR FROM transaction_date) = :year " +
            "GROUP BY EXTRACT(MONTH FROM transaction_date) " +
            "ORDER BY month", nativeQuery = true)
    List<Object[]> findMonthlyStats(Long userId, int year);

    List<Transaction> findTop5ByUserIdOrderByTransactionDateDesc(Long userId);
}