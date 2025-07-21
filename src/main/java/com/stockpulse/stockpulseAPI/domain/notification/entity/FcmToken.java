package com.stockpulse.stockpulseAPI.domain.notification.entity;

import com.stockpulse.stockpulseAPI.domain.common.BaseEntity;
import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class FcmToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 50)
    private String fcmToken;

}
