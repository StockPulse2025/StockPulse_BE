package com.stockpulse.stockpulseAPI.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 2142487942L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final com.stockpulse.stockpulseAPI.domain.common.QBaseEntity _super = new com.stockpulse.stockpulseAPI.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final ListPath<com.stockpulse.stockpulseAPI.domain.notification.entity.FcmToken, com.stockpulse.stockpulseAPI.domain.notification.entity.QFcmToken> fcmTokenList = this.<com.stockpulse.stockpulseAPI.domain.notification.entity.FcmToken, com.stockpulse.stockpulseAPI.domain.notification.entity.QFcmToken>createList("fcmTokenList", com.stockpulse.stockpulseAPI.domain.notification.entity.FcmToken.class, com.stockpulse.stockpulseAPI.domain.notification.entity.QFcmToken.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.stockpulse.stockpulseAPI.domain.stock.entity.MemberFavoriteStock, com.stockpulse.stockpulseAPI.domain.stock.entity.QMemberFavoriteStock> memberFavoriteStockList = this.<com.stockpulse.stockpulseAPI.domain.stock.entity.MemberFavoriteStock, com.stockpulse.stockpulseAPI.domain.stock.entity.QMemberFavoriteStock>createList("memberFavoriteStockList", com.stockpulse.stockpulseAPI.domain.stock.entity.MemberFavoriteStock.class, com.stockpulse.stockpulseAPI.domain.stock.entity.QMemberFavoriteStock.class, PathInits.DIRECT2);

    public final ListPath<com.stockpulse.stockpulseAPI.domain.stock.entity.MemberOwnStock, com.stockpulse.stockpulseAPI.domain.stock.entity.QMemberOwnStock> memberOwnStockList = this.<com.stockpulse.stockpulseAPI.domain.stock.entity.MemberOwnStock, com.stockpulse.stockpulseAPI.domain.stock.entity.QMemberOwnStock>createList("memberOwnStockList", com.stockpulse.stockpulseAPI.domain.stock.entity.MemberOwnStock.class, com.stockpulse.stockpulseAPI.domain.stock.entity.QMemberOwnStock.class, PathInits.DIRECT2);

    public final ListPath<com.stockpulse.stockpulseAPI.domain.news.entity.MemberScrapNews, com.stockpulse.stockpulseAPI.domain.news.entity.QMemberScrapNews> memberScrapNewsList = this.<com.stockpulse.stockpulseAPI.domain.news.entity.MemberScrapNews, com.stockpulse.stockpulseAPI.domain.news.entity.QMemberScrapNews>createList("memberScrapNewsList", com.stockpulse.stockpulseAPI.domain.news.entity.MemberScrapNews.class, com.stockpulse.stockpulseAPI.domain.news.entity.QMemberScrapNews.class, PathInits.DIRECT2);

    public final StringPath nickname = createString("nickname");

    public final ListPath<com.stockpulse.stockpulseAPI.domain.notification.entity.Notification, com.stockpulse.stockpulseAPI.domain.notification.entity.QNotification> notificationList = this.<com.stockpulse.stockpulseAPI.domain.notification.entity.Notification, com.stockpulse.stockpulseAPI.domain.notification.entity.QNotification>createList("notificationList", com.stockpulse.stockpulseAPI.domain.notification.entity.Notification.class, com.stockpulse.stockpulseAPI.domain.notification.entity.QNotification.class, PathInits.DIRECT2);

    public final com.stockpulse.stockpulseAPI.domain.notification.entity.QNotificationSetting notificationSetting;

    public final ListPath<com.stockpulse.stockpulseAPI.domain.post.entity.Post, com.stockpulse.stockpulseAPI.domain.post.entity.QPost> posts = this.<com.stockpulse.stockpulseAPI.domain.post.entity.Post, com.stockpulse.stockpulseAPI.domain.post.entity.QPost>createList("posts", com.stockpulse.stockpulseAPI.domain.post.entity.Post.class, com.stockpulse.stockpulseAPI.domain.post.entity.QPost.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.notificationSetting = inits.isInitialized("notificationSetting") ? new com.stockpulse.stockpulseAPI.domain.notification.entity.QNotificationSetting(forProperty("notificationSetting"), inits.get("notificationSetting")) : null;
    }

}

