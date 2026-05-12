package admin.skrzypekapp.controller;

import admin.skrzypekapp.dto.FinanceDashboardResponse;
import admin.skrzypekapp.dto.MonthlyStatDto;
import admin.skrzypekapp.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

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
}
