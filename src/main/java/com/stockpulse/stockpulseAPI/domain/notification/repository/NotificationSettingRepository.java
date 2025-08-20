package com.stockpulse.stockpulseAPI.domain.notification.repository;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.notification.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByMember(Member member);

    @Query("SELECT ns FROM NotificationSetting ns WHERE ns.member.id = :memberId")
    Optional<NotificationSetting> findByMemberId(Long memberId);

    boolean existsByMember(Member member);
}
