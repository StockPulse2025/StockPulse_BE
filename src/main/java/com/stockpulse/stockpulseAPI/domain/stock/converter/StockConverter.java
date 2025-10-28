package com.stockpulse.stockpulseAPI.domain.stock.converter;

import com.stockpulse.stockpulseAPI.domain.stock.dto.MarketIndexResponse;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockHistoryResponse;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockRequestDTO;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.StockTick;

import java.lang.reflect.Member;
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
            Stock stock, List<String> values, BigDecimal maxImpactRate, int newsCount) {
        return StockResponseDTO.MyStockInfluenceResponse.builder()
                .stockId(stock.getId())
                .stockName(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())
                .currentPrice(new BigDecimal(values.get(0)))
                .changeRate(new BigDecimal(values.get(1)))
                .changeAmount(new BigDecimal(values.get(2)))
                .predictInfluenceScore(maxImpactRate)
                .relatedIssueCount(newsCount)
                .build();
    }

    public static StockResponseDTO.MyStockInfluenceResponse toMyStockInfluenceResponseFallBack(
            Stock stock, StockTick latestTick, BigDecimal maxImpactRate, int newsCount) {
        return StockResponseDTO.MyStockInfluenceResponse.builder()
                .stockId(stock.getId())
                .stockName(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())
                .currentPrice(latestTick.getClosePrice())
                .changeRate(latestTick.getChangeRate())
                .changeAmount(latestTick.getChangeAmount())
                .predictInfluenceScore(maxImpactRate)
                .relatedIssueCount(newsCount)
                .build();
    }

    public static StockResponseDTO.MyStockInfluenceResponse toMyStockInfluenceResponseFault(
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

    public static StockResponseDTO.IndexDTO toIndexDTO(MarketIndexResponse response) {
        MarketIndexResponse.MarketIndexData data = response.getOutput();

        return StockResponseDTO.IndexDTO.builder()
                .currentPrice(new BigDecimal(data.getBstpNmixPrpr()))
                .changeAmount(new BigDecimal(data.getBstpNmixPrdyVrss()))
                .changeRate(new BigDecimal(data.getBstpNmixPrdyCtrt()))
                .build();
    }

    public static StockResponseDTO.MarketIndexDTO toMarketIndexDTO(
            StockResponseDTO.IndexDTO kospi, StockResponseDTO.IndexDTO kosdaq) {
        return StockResponseDTO.MarketIndexDTO.builder()
                .Kospi(kospi)
                .Kosdaq(kosdaq)
                .build();
    }

    public static StockResponseDTO.StockSimpleDTO toStockSimpleDTO(
            Stock stock, BigDecimal currentPrice, BigDecimal changeRate) {
        return StockResponseDTO.StockSimpleDTO.builder()
                .stockId(stock.getId())
                .stockName(stock.getName())
                .symbol(stock.getSymbol())
                .imageUrl(stock.getImageUrl())
                .currentPrice(currentPrice)
                .changeRate(changeRate)
                .build();
    }
}