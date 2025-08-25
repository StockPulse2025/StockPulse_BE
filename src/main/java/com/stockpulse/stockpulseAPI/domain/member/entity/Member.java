package com.stockpulse.stockpulseAPI.domain.member.entity;

import com.stockpulse.stockpulseAPI.domain.common.BaseEntity;
import com.stockpulse.stockpulseAPI.domain.member.service.event.MemberEntityListener;
import com.stockpulse.stockpulseAPI.domain.news.entity.MemberScrapNews;
import com.stockpulse.stockpulseAPI.domain.notification.entity.FcmToken;
import com.stockpulse.stockpulseAPI.domain.notification.entity.Notification;
import com.stockpulse.stockpulseAPI.domain.notification.entity.NotificationSetting;
import com.stockpulse.stockpulseAPI.domain.post.entity.Post;
import com.stockpulse.stockpulseAPI.domain.stock.entity.MemberFavoriteStock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.MemberOwnStock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(MemberEntityListener.class)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String nickname;

    @Column(nullable = false, unique = true, length = 300)
    private String email;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberFavoriteStock> memberFavoriteStockList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberOwnStock> memberOwnStockList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notificationList = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private NotificationSetting notificationSetting;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberScrapNews> memberScrapNewsList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FcmToken> fcmTokenList = new ArrayList<>();

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}