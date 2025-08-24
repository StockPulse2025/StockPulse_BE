package com.stockpulse.stockpulseAPI.domain.stock.converter;

import com.stockpulse.stockpulseAPI.domain.stock.dto.StockHistoryResponse;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockRequestDTO;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.StockTick;

import java.math.BigDecimal;
import java.util.List;

public class StockConverter {

    public static StockResponseDTO.StockSearchResultDTO toStockSearchResultDTO(Stock stock) {
        return StockResponseDTO.StockSearchResultDTO.builder()
                .stockId(stock.getId())
                .name(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())
                .build();
    }

    public static StockResponseDTO.StockDetailDTO toStockDetailDTO(
            Stock stock, List<String> values, boolean isFavorite, boolean isOwned) {
        return StockResponseDTO.StockDetailDTO.builder()
                .stockId(stock.getId())
                .name(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())
                .currentPrice(new BigDecimal(values.get(0)))
                .changeRate(new BigDecimal(values.get(1)))
                .changeAmount(new BigDecimal(values.get(2)))
                .isFavorite(isFavorite)
                .isOwned(isOwned)
                .build();
    }

    public static StockResponseDTO.StockDetailDTO toStockDetailDTOFallBack(
            Stock stock, StockTick latestTick, boolean isFavorite, boolean isOwned) {
        return StockResponseDTO.StockDetailDTO.builder()
                .stockId(stock.getId())
                .name(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())
                .currentPrice(latestTick.getClosePrice())
                .changeRate(latestTick.getChangeRate())
                .changeAmount(latestTick.getChangeAmount())
                .isFavorite(isFavorite)
                .isOwned(isOwned)
                .build();
    }

    public static StockResponseDTO.StockDetailDTO toStockDetailDTOFault(Stock stock, boolean isFavorite, boolean isOwned) {
        return StockResponseDTO.StockDetailDTO.builder()
                .stockId(stock.getId())
                .name(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())
                .currentPrice(new BigDecimal(0))
                .changeRate(new BigDecimal(0))
                .changeAmount(new BigDecimal(0))
                .isFavorite(isFavorite)
                .isOwned(isOwned)
                .build();
    }

    public static StockResponseDTO.StockRankDTO toStockRankDTO(
            Stock stock, List<String> values, boolean isFavorite, boolean isOwned) {
        return StockResponseDTO.StockRankDTO.builder()
                .rank(0)
                .stockId(stock.getId())
                .name(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())

                .currentPrice(new BigDecimal(values.get(0)))
                .changeRate(new BigDecimal(values.get(1)))
                .changeAmount(new BigDecimal(values.get(2)))
                .tradingValue(new BigDecimal(values.get(3)))
                .tradingVolume(new BigDecimal(values.get(4)))

                .isFavorite(isFavorite)
                .isOwned(isOwned)

                .build();
    }

    public static StockResponseDTO.StockRankDTO toStockRankDTOFallBack(
            Stock stock, StockTick latestTick, boolean isFavorite, boolean isOwned) {
        return StockResponseDTO.StockRankDTO.builder()
                .rank(0)
                .stockId(stock.getId())
                .name(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())

                .currentPrice(latestTick.getClosePrice())
                .changeRate(latestTick.getChangeRate())
                .changeAmount(latestTick.getChangeAmount())
                .tradingValue(latestTick.getTradingValue())
                .tradingVolume(latestTick.getTradingVolume())

                .isFavorite(isFavorite)
                .isOwned(isOwned)

                .build();
    }

    public static StockResponseDTO.StockRankDTO toStockRankDTOFault(
            Stock stock, boolean isFavorite, boolean isOwned) {
        return StockResponseDTO.StockRankDTO.builder()
                .rank(0)
                .stockId(stock.getId())
                .name(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())

                .currentPrice(new BigDecimal(0))
                .changeRate(new BigDecimal(0))
                .changeAmount(new BigDecimal(0))
                .tradingValue(new BigDecimal(0))
                .tradingVolume(new BigDecimal(0))

                .isFavorite(isFavorite)
                .isOwned(isOwned)

                .build();
    }

    public static StockResponseDTO.StockCandleDTO toStockCandleListDTO(
            StockHistoryResponse.StockHistoryDto dto){
        return StockResponseDTO.StockCandleDTO.builder()
                .date(dto.getStckBsopDate())
                .openPrice(dto.getStckOprc())
                .highPrice(dto.getStckHgpr())
                .lowPrice(dto.getStckLwpr())
                .closePrice(dto.getStckClpr())
                .totalVolume(dto.getAcmlVol())
                .build();
    }

    public static StockResponseDTO.StockCandleListDTO toStockCandleListDTO(
            Stock stock,
            StockRequestDTO.ChartPeriodType periodType,
            List<StockResponseDTO.StockCandleDTO> stockCandleDTOS
    ){
        return StockResponseDTO.StockCandleListDTO.builder()
                .stockId(stock.getId())
                .stockName(stock.getName())
                .symbol(stock.getSymbol())
                .period(periodType)
                .stockCandleDataList(stockCandleDTOS)
                .build();
    }

    public static StockResponseDTO.MyStockInfluenceResponse toMyStockInfluenceResponse(
            Stock stock, BigDecimal maxImpactRate, int newsCount) {
        return StockResponseDTO.MyStockInfluenceResponse.builder()
                .stockId(stock.getId())
                .stockName(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())
                .currentPrice(BigDecimal.ZERO)
                .changeRate(BigDecimal.ZERO)
                .changeAmount(BigDecimal.ZERO)
                .predictInfluenceScore(maxImpactRate)
                .relatedIssueCount(newsCount)
                .build();
    }
}