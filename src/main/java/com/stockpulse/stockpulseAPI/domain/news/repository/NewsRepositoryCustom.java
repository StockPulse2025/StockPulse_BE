package com.stockpulse.stockpulseAPI.domain.news.repository;

import com.stockpulse.stockpulseAPI.domain.news.dto.NewsRequestDTO;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;

import java.util.List;

public interface NewsRepositoryCustom {
    List<News> dynamicQueryWithBooleanBuilder(NewsRequestDTO.NewsFilterRequest newsFilterRequest, Long memberId);
}
