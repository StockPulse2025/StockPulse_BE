package com.stockpulse.stockpulseAPI.domain.notification.repository;

import com.stockpulse.stockpulseAPI.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
            SELECT n
            FROM Notification n
            JOIN FETCH n.impact i
            JOIN FETCH i.news ne
            JOIN FETCH i.stock s
            JOIN MemberFavoriteStock mfs ON s.id = mfs.stock.id
            WHERE n.member.id = :memberId AND mfs.member.id = :memberId
            ORDER BY n.createdAt DESC
            """
    )
    Optional<List<Notification>> findFavoriteStockHistoryByMemberId(Long memberId);

    @Query("""
            SELECT n
            FROM Notification n
            JOIN FETCH n.impact i
            JOIN FETCH i.news ne
            JOIN FETCH i.stock s
            JOIN MemberOwnStock mos ON s.id = mos.stock.id
            WHERE n.member.id = :memberId AND mos.member.id = :memberId
            ORDER BY n.createdAt DESC
            """
    )
    Optional<List<Notification>> findOwnStockHistoryByMemberId(Long memberId);
}
