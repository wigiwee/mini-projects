package com.ZorvynFinanceApp.backend.dtos;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class DashboardDto {

    int totalIncome;
    int totalExpense;
    int netBalance;
    Map<String, Integer> categoryWiseNetTotals;
    List<RecordDto> recentActivities;
    Map<YearMonth, MonthTrend> monthlyTrend; 
}
