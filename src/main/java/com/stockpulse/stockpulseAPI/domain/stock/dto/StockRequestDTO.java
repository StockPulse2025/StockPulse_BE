package com.stockpulse.stockpulseAPI.domain.stock.dto;

public class StockRequestDTO {

    public enum RealTimeChartType {
        TRADING_VALUE,
        TRADING_VOLUME,
        TOP_GAINERS,
        TOP_LOSERS
    }
}