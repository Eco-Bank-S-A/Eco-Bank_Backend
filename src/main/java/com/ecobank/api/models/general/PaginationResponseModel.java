package com.ecobank.api.models.general;

import lombok.Data;

import java.util.List;

@Data
public class PaginationResponseModel<T>{
    public PaginationResponseModel(List<T> content, int pageSize, int currentPage, int totalRecords) {
        this.content = content;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.totalRecords = totalRecords;
    }
    private List<T> content;
    private int pageSize;
    private int currentPage;
    private int totalRecords;
}
