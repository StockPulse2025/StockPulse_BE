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
            summary = "뉴스 기반 종목 영향 데이터 수신 및 저장",
            description = """
    파이프라인으로부터 전달된 뉴스 데이터를 수신하고,
    해당 뉴스의 고유 URL을 기준으로 중복 여부를 판별한 후,
    신규 뉴스 및 연관 종목에 대한 영향도 데이터를 저장합니다.
        
    해당 API는 뉴스-종목 영향도 분석 파이프라인 산출 데이터 입력 지점으로 사용됩니다.
    """
    )
    @PostMapping("/pipeline")
    public ApiResponse<String> ingestNewsImpactAndNewsData(
            @RequestBody NewsRequestDTO.NewsDataPostRequestDTO request) {
        newsCommandService.acceptDataFromPipeline(request);
        return ApiResponse.onSuccess("data added successfully");
    }

    @Operation(
            summary = "뉴스 개별 상세 조회",
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
}