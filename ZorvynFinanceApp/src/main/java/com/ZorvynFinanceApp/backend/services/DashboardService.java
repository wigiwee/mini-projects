package com.ZorvynFinanceApp.backend.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ZorvynFinanceApp.backend.dtos.DashboardDto;
import com.ZorvynFinanceApp.backend.dtos.MonthTrend;
import com.ZorvynFinanceApp.backend.dtos.RecordDto;
import com.ZorvynFinanceApp.backend.models.TransactionType;
import com.ZorvynFinanceApp.backend.models.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardService {
    
    RecordServiceImpl recordService;

    public DashboardDto getDashboard(User user){    
        DashboardDto dashboardDto = new DashboardDto();
        dashboardDto.setTotalExpense(0);
        dashboardDto.setTotalIncome(0);
        dashboardDto.setNetBalance(0);
        
        HashMap<String, Integer> categoryWiseTotal = new HashMap<>();
         
        List<RecordDto> recordDtos =  recordService.getAllRecordsList();    
        for (RecordDto recordDto : recordDtos) {
            if (recordDto.getTransactionType() == TransactionType.EXPENSE){
                dashboardDto.setTotalExpense(dashboardDto.getTotalExpense()+recordDto.getAmount());
                dashboardDto.setNetBalance(dashboardDto.getNetBalance() - recordDto.getAmount());
                categoryWiseTotal.put(
                    recordDto.getCategory().name(),  
                    categoryWiseTotal.getOrDefault(recordDto.getCategory().name(), 0) - recordDto.getAmount());
                
            }else if(recordDto.getTransactionType() == TransactionType.INCOME){
                dashboardDto.setTotalIncome(dashboardDto.getTotalIncome()+recordDto.getAmount());
                dashboardDto.setNetBalance(dashboardDto.getNetBalance() + recordDto.getAmount());
                categoryWiseTotal.put(
                    recordDto.getCategory().name(),
                    categoryWiseTotal.getOrDefault(recordDto.getCategory().name(), 0) + recordDto.getAmount());
            }
        }
        
        Date now = new Date();
        Date weekAgo = Date.from(LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<RecordDto> recentActivity = recordDtos.stream()
            .filter(r-> r.getDate().before(now) && r.getDate().after(weekAgo))
            .toList();
        
        Map<YearMonth, List<RecordDto>> groupedByYearMonth =
            recordDtos.stream()
                .collect(Collectors.groupingBy(record ->
                    YearMonth.from(record.getDate().toLocalDate())
        ));
        Map<YearMonth,  MonthTrend> monthlyTrend = new HashMap<>();
        groupedByYearMonth.forEach(
            (yearMonth, monthlyRecords)->{
                int income = 0;
                int expense = 0;
                int netBalance = 0;
                for (RecordDto recordDto : monthlyRecords) {
                    if (recordDto.getTransactionType() == TransactionType.EXPENSE){
                        expense += recordDto.getAmount();
                        netBalance -= recordDto.getAmount();
                    }else if(recordDto.getTransactionType() == TransactionType.INCOME){
                        income += recordDto.getAmount();
                        netBalance += recordDto.getAmount();
                    }
                }
                monthlyTrend.put(yearMonth, new MonthTrend(income, expense, netBalance));
            }
        );

        dashboardDto.setCategoryWiseNetTotals(categoryWiseTotal);

        //one month recent activity
        // dashboardDto.setRecentActivities(groupedByYearMonth.get(YearMonth.now()));

        //one week recent activity
        dashboardDto.setRecentActivities(recentActivity);

        dashboardDto.setMonthlyTrend(monthlyTrend);
        
        return dashboardDto;
    }
}
