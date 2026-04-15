package com.ZorvynFinanceApp.backend.models;


import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "records")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    
    @Column(nullable = false)
    private int amount;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;
    
    @Column(nullable = false)
    private Date date;
    
    @Column(nullable = true)
    private String description;
}
