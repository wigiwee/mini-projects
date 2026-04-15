package com.ZorvynFinanceApp.backend.services;


import java.util.List;

import com.ZorvynFinanceApp.backend.dtos.CustomPageResponse;
import com.ZorvynFinanceApp.backend.dtos.RecordDto;

public interface RecordService {
    
    void createRecord(RecordDto recordDto);
    
    RecordDto getRecord(Long recordId);
    
    CustomPageResponse<RecordDto> getAllRecords(int pageNumber, int pageSize, String sortBy, String sortSeq, String category, Double minAmount, Double maxAmount, String type);
    
    List<RecordDto> getAllRecordsList();
    
    RecordDto updateRecord(RecordDto recordDto,Long recordId);
    
    RecordDto deleteRecord(Long recordId);

    List<RecordDto> searchRecord(String query);
}
