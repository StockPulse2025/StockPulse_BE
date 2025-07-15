package com.stockpulse.stockpulseAPI.domain.notification.entity;

import com.stockpulse.stockpulseAPI.domain.common.BaseEntity;
import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
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
}
