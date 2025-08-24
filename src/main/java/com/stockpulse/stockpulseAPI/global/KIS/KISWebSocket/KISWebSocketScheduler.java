package com.stockpulse.stockpulseAPI.global.KIS.KISWebSocket;

import com.stockpulse.stockpulseAPI.domain.stock.service.StockCommandService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class KISWebSocketScheduler {

    private final KISWebSocketClientConfig kisWebSocketClientConfig;
    private final StockCommandService stockCommandService;
    private boolean isConnected = false;

    @PostConstruct
    public void initializeConnections() {
        checkTimeAndManageConnections();
    }

    @Scheduled(cron = "0 0 9 ? * MON-FRI")
    public void startWebSocketConnections() {
        if (!isConnected) {
            log.info("Starting KIS WebSocket connections at 9:00 AM");
            kisWebSocketClientConfig.startConnections();
            isConnected = true;
        }
    }

    @Scheduled(cron = "0 30 15 ? * MON-FRI")
    public void stopWebSocketConnections() {
        if (isConnected) {
            log.info("Stopping KIS WebSocket connections at 3:30 PM");
            kisWebSocketClientConfig.stopConnections();
            stockCommandService.saveRedisStockTickDataToDB();
            isConnected = false;
        }
    }

    @Scheduled(fixedRate = 60000)
    public void checkTimeAndManageConnections() {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            if (isConnected) {
                log.info("Stopping WebSocket connections - Weekend");
                kisWebSocketClientConfig.stopConnections();
                isConnected = false;
            }
            return;
        }
        
        LocalTime timeNow = now.toLocalTime();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(15, 30);

        if (timeNow.isAfter(startTime) && timeNow.isBefore(endTime) && !isConnected) {
            log.info("Starting WebSocket connections within trading hours");
            kisWebSocketClientConfig.startConnections();
            isConnected = true;
        } else if ((timeNow.isBefore(startTime) || timeNow.isAfter(endTime)) && isConnected) {
            log.info("Stopping WebSocket connections outside trading hours");
            kisWebSocketClientConfig.stopConnections();
            isConnected = false;
        }
    }
}