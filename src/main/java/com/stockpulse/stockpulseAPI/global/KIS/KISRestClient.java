package com.stockpulse.stockpulseAPI.global.KIS;

import com.stockpulse.stockpulseAPI.domain.stock.converter.StockConverter;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockHistoryResponse;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockRequestDTO;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.dto.TokenResponse;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KISRestClient {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${kis.app_key1}")
    private String appkey;

    @Value("${kis.app_secret1}")
    private String appsecret;

    private final String KIS_API_URL
            = "https://openapi.koreainvestment.com:9443";

    public StockResponseDTO.StockCandleListDTO getStockCandleListDTO(Stock stock, StockRequestDTO.ChartPeriodType period) {
        DateRange dateRange = calculateDateRange(period);
        
        String accessToken = getAccessTokenFromCache();
        StockHistoryResponse response = callKISApiWithRetry(stock, dateRange, accessToken);
        
        return buildStockCandleListDTO(stock, period, response);
    }
    
    private DateRange calculateDateRange(StockRequestDTO.ChartPeriodType period) {
        String endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String periodCode;
        String startDate;
        
        switch (period) {
            case DAY:
                periodCode = "D";
                startDate = LocalDate.now().minusMonths(5)
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                break;
            case WEEK:
                periodCode = "W";
                startDate = LocalDate.now().minusYears(1)
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                break;
            case MONTH:
                periodCode = "M";
                startDate = LocalDate.now().minusYears(1)
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                break;
            default:
                periodCode = "D";
                startDate = LocalDate.now().minusMonths(5)
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                break;
        }
        
        return new DateRange(periodCode, startDate, endDate);
    }
    
    private String getAccessTokenFromCache() {
        String accessToken = (String) redisTemplate.opsForValue().get("kis_access_token");
        return accessToken != null ? accessToken : getAuthorizationToken();
    }
    
    private StockHistoryResponse callKISApiWithRetry(Stock stock, DateRange dateRange, String accessToken) {
        try {
            return callKISApi(stock, dateRange, accessToken);
        } catch (Exception e) {
            String newToken = getAuthorizationToken();
            return callKISApi(stock, dateRange, newToken);
        }
    }
    
    private StockHistoryResponse callKISApi(Stock stock, DateRange dateRange, String accessToken) {
        WebClient webClient = createWebClient(accessToken);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .queryParam("FID_INPUT_ISCD", stock.getSymbol())
                        .queryParam("FID_INPUT_DATE_1", dateRange.startDate)
                        .queryParam("FID_INPUT_DATE_2", dateRange.endDate)
                        .queryParam("FID_PERIOD_DIV_CODE", dateRange.periodCode)
                        .queryParam("FID_ORG_ADJ_PRC", "1")
                        .build())
                .retrieve()
                .bodyToMono(StockHistoryResponse.class)
                .block();
    }
    
    private WebClient createWebClient(String accessToken) {
        return WebClient.builder()
                .baseUrl(KIS_API_URL)
                .defaultHeader("content-type", "application/json")
                .defaultHeader("authorization", "Bearer " + accessToken)
                .defaultHeader("appkey", appkey)
                .defaultHeader("appsecret", appsecret)
                .defaultHeader("tr_id", "FHKST03010100")
                .defaultHeader("custtype", "P")
                .build();
    }
    
    private StockResponseDTO.StockCandleListDTO buildStockCandleListDTO(Stock stock, StockRequestDTO.ChartPeriodType period, StockHistoryResponse response) {
        List<StockResponseDTO.StockCandleDTO> candleList = response.getOutput2().stream()
                .map(StockConverter::toStockCandleListDTO)
                .collect(Collectors.toList());

        return StockConverter.toStockCandleListDTO(stock, period, candleList);
    }
    
    private static class DateRange {
        final String periodCode;
        final String startDate;
        final String endDate;
        
        DateRange(String periodCode, String startDate, String endDate) {
            this.periodCode = periodCode;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    public String getAuthorizationToken(){
        WebClient webClient = WebClient.builder()
                .baseUrl(KIS_API_URL)
                .defaultHeader("content-type", "application/json")
                .build();

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("appkey", appkey);
        requestBody.put("appsecret", appsecret);

        TokenResponse response = webClient.post()
                .uri("/oauth2/tokenP")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();

        String accessToken = response.getAccessToken();

        redisTemplate.opsForValue().set(
                "kis_access_token",
                accessToken,
                Duration.ofHours(23).plusMinutes(50));
        
        return accessToken;
    }
}