package com.ZorvynFinanceApp.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthTrend{
    private int income;
    private int expense;
    private int netBalance;    
}
