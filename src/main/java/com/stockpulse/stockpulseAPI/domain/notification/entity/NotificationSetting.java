package com.stockpulse.stockpulseAPI.domain.notification.entity;

import com.stockpulse.stockpulseAPI.domain.common.BaseEntity;
import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationSettingRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean ownStock;

    @Column(nullable = false)
    private Boolean interestStock;

    @Column(nullable = false)
    private Boolean goodNews;

    @Column(nullable = false)
    private Boolean badNews;

    @Column(nullable = false)
    private Boolean neutralNews;

    @Column(nullable = false)
    private BigDecimal goodSensitivity1;

    @Column(nullable = false)
    private BigDecimal badSensitivity1;

    @Column(nullable = false)
    private BigDecimal goodSensitivity2;

    @Column(nullable = false)
    private BigDecimal badSensitivity2;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void updateSettings(NotificationSettingRequestDTO.UpdateDTO request) {
        this.ownStock = request.getOwnStock();
        this.interestStock = request.getInterestStock();
        this.goodNews = request.getGoodNews();
        this.badNews = request.getBadNews();
        this.neutralNews = request.getNeutralNews();
        this.goodSensitivity1 = request.getGoodSensitivity1();
        this.badSensitivity1 = request.getBadSensitivity1();
        this.goodSensitivity2 = request.getGoodSensitivity2();
        this.badSensitivity2 = request.getBadSensitivity2();
    }

    public void resetSetting() {
        this.ownStock = false;
        this.interestStock = false;
        this.goodNews = false;
        this.badNews = false;
        this.neutralNews = false;
        this.goodSensitivity1 = BigDecimal.valueOf(0.5);
        this.badSensitivity1 = BigDecimal.valueOf(0.5);
        this.goodSensitivity2 = BigDecimal.valueOf(2.0);
        this.badSensitivity2 = BigDecimal.valueOf(2.0);
    }
}
