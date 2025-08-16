package com.stockpulse.stockpulseAPI.domain.stock.service;

import com.stockpulse.stockpulseAPI.domain.stock.converter.StockConverter;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockQueryService {

    private final StockRepository stockRepository;

    public List<StockResponseDTO.StockSearchResultDTO> searchStocks(String keyword) {

        String trimmedKeyword = keyword.trim();
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Stock> searchResult = stockRepository
                .searchStocks(trimmedKeyword);

        return searchResult.stream()
                .map(StockConverter::toStockSearchResultDTO)
                .collect(Collectors.toList());
    }
}