package com.stockpulse.stockpulseAPI.domain.news.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stockpulse.stockpulseAPI.domain.news.dto.NewsRequestDTO;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.entity.QNews;
import com.stockpulse.stockpulseAPI.domain.news.entity.QImpact;
import com.stockpulse.stockpulseAPI.domain.stock.entity.QStock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.QCategory;
import com.stockpulse.stockpulseAPI.domain.stock.entity.QStockCategory;
import com.stockpulse.stockpulseAPI.domain.stock.entity.QMemberFavoriteStock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.QMemberOwnStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NewsRepositoryCustomImpl implements NewsRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QNews qNews = QNews.news;
    private final QImpact qImpact = QImpact.impact;
    private final QStock qStock = QStock.stock;
    private final QCategory qCategory = QCategory.category;
    private final QStockCategory qStockCategory = QStockCategory.stockCategory;
    private final QMemberFavoriteStock qMemberFavoriteStock = QMemberFavoriteStock.memberFavoriteStock;
    private final QMemberOwnStock qMemberOwnStock = QMemberOwnStock.memberOwnStock;

    @Override
    public List<News> dynamicQueryWithBooleanBuilder(NewsRequestDTO.NewsFilterRequest newsFilterRequest, Long memberId) {
        BooleanBuilder predicate = new BooleanBuilder();

        var query = queryFactory
            .selectFrom(qNews)
            .leftJoin(qNews.impacts, qImpact)
            .leftJoin(qImpact.stock, qStock)
            .leftJoin(qStock.stockCategories, qStockCategory)
            .leftJoin(qStockCategory.category, qCategory)
            .distinct();

        // 정렬 조건
        OrderSpecifier<?> orderSpecifier = qNews.publishedDate.desc();
        
        if(newsFilterRequest.getSort() != null) {
            NewsRequestDTO.NewsFilterRequest.SortType sortType = newsFilterRequest.getSort();
            switch (sortType){
                case LATEST:
                    orderSpecifier = qNews.publishedDate.desc();
                    break;
                case IMPACT:
                    orderSpecifier = qNews.publishedDate.desc();
                    break;
            }
        }

        // 종목 필터링
        if(newsFilterRequest.getAllStock() != null && newsFilterRequest.getAllStock()) {

        }
        
        // 보유 종목 필터링
        if(newsFilterRequest.getOwnedStock() != null && newsFilterRequest.getOwnedStock()) {
            predicate.and(qImpact.stock.id.in(
                JPAExpressions.select(qMemberOwnStock.stock.id)
                    .from(qMemberOwnStock)
                    .where(qMemberOwnStock.member.id.eq(memberId))
            ));
        }
        
        // 관심 종목 필터링
        if(newsFilterRequest.getFavoriteStock() != null && newsFilterRequest.getFavoriteStock()) {
            predicate.and(qImpact.stock.id.in(
                JPAExpressions.select(qMemberFavoriteStock.stock.id)
                    .from(qMemberFavoriteStock)
                    .where(qMemberFavoriteStock.member.id.eq(memberId))
            ));
        }

        // 산업군 필터링
        if(newsFilterRequest.getIndustries() != null && !newsFilterRequest.getIndustries().isEmpty()) {
            predicate.and(qCategory.name.in(newsFilterRequest.getIndustries()));
        }

        // 민감도 필터링
        BooleanBuilder sensitivityPredicate = new BooleanBuilder();
        
        // 호재 필터
        if(newsFilterRequest.getPositive() != null && newsFilterRequest.getPositive().isEnabled()) {
            var positive = newsFilterRequest.getPositive();
            sensitivityPredicate.or(
                qImpact.impactRate.between(
                    java.math.BigDecimal.valueOf(positive.getMinImpact()),
                    java.math.BigDecimal.valueOf(positive.getMaxImpact())
                )
            );
        }
        
        // 악재 필터
        if(newsFilterRequest.getNegative() != null && newsFilterRequest.getNegative().isEnabled()) {
            var negative = newsFilterRequest.getNegative();
            sensitivityPredicate.or(
                qImpact.impactRate.between(
                    java.math.BigDecimal.valueOf(-negative.getMaxImpact()),
                    java.math.BigDecimal.valueOf(-negative.getMinImpact())
                )
            );
        }
        
        // 중립 필터
        if(newsFilterRequest.getNeutral() != null && newsFilterRequest.getNeutral()) {
            sensitivityPredicate.or(
                qImpact.impactRate.between(
                    java.math.BigDecimal.valueOf(-0.5),
                    java.math.BigDecimal.valueOf(0.5)
                )
            );
        }
        
        if(sensitivityPredicate.hasValue()) {
            predicate.and(sensitivityPredicate);
        }

        return query
            .where(predicate)
            .orderBy(orderSpecifier)
            .fetch();
    }
}
