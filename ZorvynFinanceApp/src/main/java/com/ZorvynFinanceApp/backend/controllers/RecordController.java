package com.ZorvynFinanceApp.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ZorvynFinanceApp.backend.configurations.ApplicationConstants;
import com.ZorvynFinanceApp.backend.dtos.CustomPageResponse;
import com.ZorvynFinanceApp.backend.dtos.RecordDto;
import com.ZorvynFinanceApp.backend.services.RecordServiceImpl;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@Controller
@RequestMapping("/api/v1/records")
@CrossOrigin
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordController {

    RecordServiceImpl recordServiceImpl;

    @GetMapping
    public ResponseEntity<CustomPageResponse<RecordDto>> getMethodName(
            @RequestParam(name = "pageNumber", required = false, defaultValue = ApplicationConstants.DEFAULT_PAGE_NUMBER) int pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = ApplicationConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(name = "sortBy", required = false, defaultValue = ApplicationConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(name = "sortSeq", required = false, defaultValue = ApplicationConstants.DEFAULT_SORT_SEQ) String sortSeq,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) String type
        ) {        
        return ResponseEntity.status(HttpStatus.OK).body(recordServiceImpl.getAllRecords(pageNumber, pageSize, sortBy, sortSeq, category, minAmount, maxAmount, type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecordDto> getRecord (@PathVariable Long  id) {
        return ResponseEntity.ok().body(recordServiceImpl.getRecord(id));
    }
    
    @PostMapping
    public ResponseEntity<String>  saveRecord(@Valid @RequestBody RecordDto recordDto) {
        recordServiceImpl.createRecord(recordDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecordDto> updateRecord(@PathVariable Long id, @Valid @RequestBody RecordDto recordDto) {
        RecordDto updatedDto = recordServiceImpl.updateRecord(recordDto, id);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedDto);   
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<RecordDto> deleteRecord(@PathVariable Long id ){
        RecordDto deletedRecordDto = recordServiceImpl.deleteRecord(id);
        return ResponseEntity.status(HttpStatus.OK).body(deletedRecordDto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecordDto>> search (@RequestParam String query ) {
        return ResponseEntity.status(HttpStatus.OK).body(recordServiceImpl.searchRecord(query));
    }
}
