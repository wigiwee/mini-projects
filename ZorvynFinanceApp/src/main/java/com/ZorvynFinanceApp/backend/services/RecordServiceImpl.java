package com.ZorvynFinanceApp.backend.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ZorvynFinanceApp.backend.dtos.CustomPageResponse;
import com.ZorvynFinanceApp.backend.dtos.RecordDto;
import com.ZorvynFinanceApp.backend.exceptions.ResourceNotFoundException;
import com.ZorvynFinanceApp.backend.models.Record;
import com.ZorvynFinanceApp.backend.models.RecordSpecification;
import com.ZorvynFinanceApp.backend.repositories.RecordsRepo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordServiceImpl implements RecordService {


    RecordsRepo recordsRepo;

    ModelMapper modelMapper;

    @Override
    public void createRecord(RecordDto recordDto) {
        System.out.println(recordDto);
         recordsRepo.save(dtoToEntity(recordDto));

    }


    @Override
    public List<RecordDto> getAllRecordsList() {
        List<Record> records = recordsRepo.findAll();
        List<RecordDto> recordDtos = records.stream()
                .map(record -> entityToDto(record))
                .toList();
        return recordDtos;
    }
    
    @Override
    public CustomPageResponse<RecordDto> getAllRecords(int pageNumber, int pageSize, String sortBy, String sortSeq, String category, Double minAmount, Double maxAmount, String type) {
        Sort sort;
    
        if (sortSeq.equals("descending")){
            sort = Sort.by(sortBy).descending();

        }else{
            sort = Sort.by(sortBy).ascending();
        }

        PageRequest pageRequest = PageRequest.of(pageNumber -1, pageSize, sort);
        Page<Record> recordsPage;
        if (category == null && maxAmount == null && minAmount == null && category == null){
            recordsPage = recordsRepo.findAll(pageRequest);
        }else{
            recordsPage = recordsRepo.findAll(RecordSpecification.filter(category, minAmount, maxAmount, type), pageRequest);
        }

        List<Record> records = recordsPage.getContent();
        List<RecordDto> recordDtos = records
                .stream()
                .map(record -> entityToDto(record))
                .toList();
        
        return CustomPageResponse
            .<RecordDto>builder()
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .totalElements(recordsPage.getTotalElements())
            .totalPages(recordsPage.getTotalPages())
            .isLast(recordsPage.isLast())
            .content(recordDtos)
            .build();
    }

    @Override
    public RecordDto getRecord(Long recordId) {
        Record record = recordsRepo.findById(recordId).orElseThrow(()->new ResourceNotFoundException("Record with provided ID not found ", recordId));
        return entityToDto(record);
    }

    @Override
    public RecordDto updateRecord(RecordDto recordDto,Long recordId) {
        Record previousRecord = recordsRepo.findById(recordId).orElseThrow(()->new ResourceNotFoundException("Record with provided ID not found ", recordId));
        recordDto.setId(recordId);
        recordsRepo.save(dtoToEntity(recordDto));
        return entityToDto(previousRecord);
    }

    @Override
    public RecordDto deleteRecord(Long recordId) {
        Record record = recordsRepo.findById(recordId).orElseThrow(() -> new ResourceNotFoundException("record with provided id not found", recordId));

        recordsRepo.delete(record);
        return entityToDto(record);
    }

    @Override
    public List<RecordDto> searchRecord(String query) {
        List<Record> records = recordsRepo.searchRecords(query);
        return records.stream()
            .map( record -> entityToDto(record))
            .toList();
    }

    public RecordDto entityToDto(Record record){
        return modelMapper.map(record, RecordDto.class);
    }
    public Record dtoToEntity(RecordDto recordDto){
        return modelMapper.map(recordDto, Record.class);        
    }
}

