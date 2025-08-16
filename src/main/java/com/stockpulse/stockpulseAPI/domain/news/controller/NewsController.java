package com.stockpulse.stockpulseAPI.domain.news.controller;

import com.stockpulse.stockpulseAPI.domain.news.dto.NewsRequestDTO;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsResponseDTO;
import com.stockpulse.stockpulseAPI.domain.news.service.NewsCommandService;
import com.stockpulse.stockpulseAPI.domain.news.service.NewsQueryService;
import com.stockpulse.stockpulseAPI.global.apiPayload.ApiResponse;
import com.stockpulse.stockpulseAPI.global.security.handler.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class NewsController {

    private final NewsCommandService newsCommandService;
    private final NewsQueryService newsQueryService;

    @Operation(
            summary = "뉴스 기반 종목 영향 데이터 수신 및 저장 - 단건",
            description = """
    파이프라인으로부터 전달된 단일 뉴스 데이터를 수신하고,
    해당 뉴스의 고유 URL을 기준으로 중복 여부를 판별한 후,
    신규 뉴스 및 연관 종목에 대한 영향도 데이터를 저장합니다.
        
    해당 API는 뉴스-종목 영향도 분석 파이프라인 단건 데이터 입력 지점으로 사용됩니다.
    """
    )
    @PostMapping("/pipeline/single")
    public ApiResponse<String> ingestSingleNewsImpactData(
            @RequestBody NewsRequestDTO.NewsDataPostRequestDTO request) {
        newsCommandService.acceptDataFromPipeline(request);
        return ApiResponse.onSuccess("single news data added successfully");
    }

    @Operation(
            summary = "뉴스 기반 종목 영향 데이터 수신 및 저장 -  복수건",
            description = """
    파이프라인으로부터 전달된 복수 뉴스 데이터를 일괄 수신하고,
    각 뉴스의 고유 URL을 기준으로 중복 여부를 판별한 후,
    신규 뉴스 및 연관 종목에 대한 영향도 데이터를 저장합니다.
        
    해당 API는 뉴스-종목 영향도 분석 파이프라인 배치 데이터 입력 지점으로 사용됩니다.
    """
    )
    @PostMapping("/pipeline/batch")
    public ApiResponse<String> ingestBatchNewsImpactData(
            @RequestBody NewsRequestDTO.NewsDataBatchPostRequestDTO request) {
        newsCommandService.acceptBatchDataFromPipeline(request);
        return ApiResponse.onSuccess("batch news data added successfully");
    }

    @Operation(
            summary = "뉴스 개별 상세 조회 API",
            description = """
    - 뉴스를 개별 상세조회 합니다. 조회하고자 하는 뉴스의 Id를 넘겨주세요
    - 뉴스 상세 정보(제목, 이미지, 호재/악재 여부 등)과 영향도 순 종목 순위와 정보가 반환됩니다.
    """
    )
    @GetMapping("/{newsId}")
    public ApiResponse<NewsResponseDTO.NewsDetailResponseDTO> getNewsDetail(
            @AuthUser Long memberId,
            @PathVariable("newsId") Long newsId) {
        NewsResponseDTO.NewsDetailResponseDTO result
                = newsQueryService.getNewsDetail(newsId,memberId);
        return ApiResponse.onSuccess(result);
    }

    @Operation(
            summary = "뉴스 스크랩 토글 API",
            description = "특정 뉴스에 대해 사용자의 스크랩을 추가하거나 취소합니다. " +
                    "해당 뉴스를 스크랩하지 않은 경우 스크랩이 추가되고, 이미 스크랩한 경우 스크랩이 해제됩니다. " +
                    "토글 후 현재 스크랩 상태를 반환합니다. 스크랩할 뉴스 ID를 전달해주세요."
    )
    @PostMapping("/{newsId}/scrap")
    public ApiResponse<NewsResponseDTO.ScrapResultDTO> toggleScrapNews(
            @AuthUser Long memberId,
            @PathVariable("newsId") Long newsId) {
        NewsResponseDTO.ScrapResultDTO result
                = newsCommandService.toggleNewsScrap(newsId,memberId);
        return ApiResponse.onSuccess(result);
    }

    @Operation(
            summary = "뉴스룸 메인 뉴스 조회 API",
            description = """
    - 뉴스룸 진입시, 제일 메인에 게시되는 뉴스를 조회합니다.
    - 호출된 날에서 예측 등락폭(뉴스로 인한 주가 영향도 예측치)가장 큰 뉴스를 조회합니다.
    """
    )
    @GetMapping("/main")
    public ApiResponse<NewsResponseDTO.NewsOverviewDTO> getNewsDetail(@AuthUser Long memberId) {
        NewsResponseDTO.NewsOverviewDTO result = newsQueryService.getMainNews(memberId);
        return ApiResponse.onSuccess(result);
    }
}