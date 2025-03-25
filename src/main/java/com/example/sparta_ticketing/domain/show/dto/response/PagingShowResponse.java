package com.example.sparta_ticketing.domain.show.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PagingShowResponse {
    private List<ShowResponseDto> shows;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
}
