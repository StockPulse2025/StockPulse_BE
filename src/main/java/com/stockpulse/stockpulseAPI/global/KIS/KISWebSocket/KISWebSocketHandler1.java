package com.stockpulse.stockpulseAPI.global.KIS.KISWebSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KISWebSocketHandler1 extends TextWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;

    private static final List<String> STOCK_CODE = List.of(
            "005930", "000660", "373220", "207940", "005380",
            "012450", "329180", "105560", "000270", "034020",
            "068270", "035420", "042660", "055550", "028260",
            "012330", "035720", "009540", "032830", "005490",
            "086790", "011200", "015760", "051910", "064350",
            "000810", "138040", "402340", "316140", "267260",
            "010140", "033780", "006400", "096770", "010130",
            "024110", "259960", "034730", "030200", "079550"
    );
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${kis.approval_key1}")
    private String approvalKey;
    private String custType = "P";
    private String trType = "1";
    private String trId = "H0STCNT0";

    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("KIS connect : {}", session.getId());

        for (String stockCode : STOCK_CODE) {
            String requestMessage = buildSubscribeMessage(stockCode);

            session.sendMessage(new TextMessage(requestMessage));
            log.info("구독 요청 메시지 전송 ws1 - 종목 코드: {}", stockCode);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        if (payload.contains("\"tr_id\":\"PINGPONG\"")) {
            session.sendMessage(new TextMessage("{\"header\":{\"tr_id\":\"PINGPONG\"}}"));
            return;
        }

        if (payload.startsWith("0|H0STCNT0|")) {
            processTickData(payload);
        }
    }

    private static final int EXPECTED_FIELD_COUNT = 46;

    private void processTickData(String rawMessage) {
        try {
            String[] parts = rawMessage.split("\\|");
            if (parts.length < 4) {
                log.warn("잘못된 체결 메시지: {}", rawMessage);
                return;
            }

            int dataCount = Integer.parseInt(parts[2]);
            String body = parts[3];
            for (int i = 4; i < parts.length; i++) {
                body += "^" + parts[i];
            }
            String[] fields = body.split("\\^");

            for (int i = 0; i < dataCount; i++) {
                int startIdx = i * EXPECTED_FIELD_COUNT;

                String MKSC_SHRN_ISCD = fields[startIdx];
                String STCK_PRPR = fields[startIdx + 2];
                String PRDY_CTRT = fields[startIdx + 5];
                String WGHN_AVRG_STCK_PRC = fields[startIdx + 6];
                String STCK_OPRC = fields[startIdx + 7];
                String STCK_HGPR = fields[startIdx + 8];
                String STCK_LWPR = fields[startIdx + 9];
                String ACML_VOL = fields[startIdx + 13];
                String ACML_TR_PBMN = fields[startIdx + 14];

                // Redis 저장
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("date", LocalDateTime.now().toString());
                updateMap.put("openPrice", STCK_OPRC);
                updateMap.put("highPrice", STCK_HGPR);
                updateMap.put("lowPrice", STCK_LWPR);
                updateMap.put("closePrice", STCK_PRPR);
                updateMap.put("tradingValue", ACML_TR_PBMN);
                updateMap.put("tradingVolume", ACML_VOL);
                updateMap.put("changeRate", PRDY_CTRT);
                updateMap.put("changeAmount", WGHN_AVRG_STCK_PRC);

                String stockKey = "stock:tick:" + MKSC_SHRN_ISCD;
                redisTemplate.opsForHash().putAll(stockKey, updateMap);

                // WebSocket 전송
                StockResponseDTO.StockTickDataDTO stockData = StockResponseDTO.StockTickDataDTO.builder()
                        .symbol(MKSC_SHRN_ISCD)
                        .currentPrice(new BigDecimal(STCK_PRPR))
                        .changeRate(new BigDecimal(PRDY_CTRT))
                        .changeAmount(new BigDecimal(WGHN_AVRG_STCK_PRC))
                        .build();

                messagingTemplate.convertAndSend("/sub/" + MKSC_SHRN_ISCD, stockData);
            }
        } catch (Exception e) {
            log.error("체결 데이터 파싱 실패", e);
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        int statusCode = status.getCode();

        if (statusCode == CloseStatus.NORMAL.getCode()) {
            log.info("정상적으로 웹소켓 연결이 종료되었습니다.");
        } else if (statusCode == CloseStatus.SESSION_NOT_RELIABLE.getCode()) {
            log.warn("웹소켓 세션이 안정적이지 않아 연결이 종료되었습니다.");
        } else {
            log.error("웹소켓 연결이 비정상적으로 종료되었습니다. Status Code: {}, Reason: {}", statusCode, status.getReason());
        }
        log.info("KIS 웹소켓 연결 종료 : {}", status.getReason());
    }

    private String buildSubscribeMessage(String stockCode) {
        return String.format("""
            {
                "header": {
                    "approval_key": "%s",
                    "custtype": "%s",
                    "tr_type": "%s",
                    "tr_id": "%s",
                    "tr_key": "%s"
                },
                "body": {
                    "input": {
                        "tr_id": "%s",
                        "tr_key": "%s"
                    }
                }
            }
            """,
                approvalKey, custType, trType, trId, stockCode,
                trId, stockCode
        );
    }
}