package com.stockpulse.stockpulseAPI.domain.stock.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.news.repository.MemberScrapNewsRepository;
import com.stockpulse.stockpulseAPI.domain.stock.dto.StockResponseDTO;
import com.stockpulse.stockpulseAPI.domain.stock.entity.MemberFavoriteStock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.repository.MemberFavoriteStockRepository;
import com.stockpulse.stockpulseAPI.domain.stock.repository.MemberOwnStockRepository;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockRepository;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.status.ErrorStatus;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.MemberHandler;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler.StockHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockCommandService {

    private final StockRepository stockRepository;
    private final MemberRepository memberRepository;
    private final MemberFavoriteStockRepository memberFavoriteStockRepository;
    private final MemberOwnStockRepository memberOwnStockRepository;

    public StockResponseDTO.StockFavoriteStatusDTO toggleStockFavorite(Long memberId, Long stockId) {

        Member member = findMemberByIdOrThrow(memberId);
        Stock stock = findStockByIdOrThrow(stockId);
        Optional<MemberFavoriteStock> favoriteStockOpt = memberFavoriteStockRepository.findByMemberAndStock(member, stock);

        if(favoriteStockOpt.isPresent()) {
            memberFavoriteStockRepository.delete(favoriteStockOpt.get());
            return new StockResponseDTO.StockFavoriteStatusDTO(false);
        }else {
            memberFavoriteStockRepository.save(MemberFavoriteStock.builder()
                    .member(member)
                    .stock(stock)
                    .build());
            return new StockResponseDTO.StockFavoriteStatusDTO(true);
        }
    }

    public Member findMemberByIdOrThrow(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public Stock findStockByIdOrThrow(Long StockId) {
        return stockRepository.findById(StockId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.STOCK_NOT_FOUND));
    }
}
