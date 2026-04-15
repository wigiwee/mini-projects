package com.ZorvynFinanceApp.backend.dtos;

import java.sql.Date;

import com.ZorvynFinanceApp.backend.models.Category;
import com.ZorvynFinanceApp.backend.models.TransactionType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RecordDto {

    private Long id; 
    
    @NotNull
    @Min(value = 1, message = "amount can't be negative")
    private int amount;
    
    @NotNull
    private TransactionType transactionType;
    
    @NotNull
    private Category category;

    @NotNull
    @PastOrPresent(message = "date cannot be in the future")
    private Date date;
    
    @Size(min = 0, max = 150, message = "description can't be more than 150 characters")
    private String description;

}
