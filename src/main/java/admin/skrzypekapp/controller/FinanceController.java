package admin.skrzypekapp.controller;

import admin.skrzypekapp.dto.CreateTransactionRequest;
import admin.skrzypekapp.dto.FinanceDashboardResponse;
import admin.skrzypekapp.dto.MonthlyStatDto;
import admin.skrzypekapp.service.FinanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/finances")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/dashboard")
    public ResponseEntity<FinanceDashboardResponse> getDashboardData(){
        Long userId = 4L;
        return ResponseEntity.ok(financeService.getDashboardData(userId));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<MonthlyStatDto>> getMonthlyStatData(@RequestParam(required = false) Integer year){
        Long userId = 4L;
        int targetYear = (year != null) ? year : Year.now().getValue();
        return ResponseEntity.ok(financeService.getMonthlyStats(userId, targetYear));
    }
    @PostMapping("/transactions")
    public ResponseEntity<?> addTransaction(@Valid @RequestBody CreateTransactionRequest request){
        Long userId = 4L;

        financeService.addTransaction(
                userId,
                request.accountId(),
                request.amount(),
                request.type(),
                request.category(),
                request.description(),
                request.transactionDate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id){
        financeService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<Void> updateTransaction(
            @PathVariable UUID id,
            @Valid @RequestBody CreateTransactionRequest request
    ){
        financeService.updateTransaction(id, request);
        return ResponseEntity.ok().build();
    }
}
