package com.ZorvynFinanceApp.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ZorvynFinanceApp.backend.models.Record;

@Repository
public interface RecordsRepo extends JpaRepository<Record,Long>, JpaSpecificationExecutor<Record> {
    @Query("""
        SELECT r FROM Record r WHERE 
        LOWER(r.transactionType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(r.category) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Record> searchRecords(@Param("keyword") String keyword);
}
