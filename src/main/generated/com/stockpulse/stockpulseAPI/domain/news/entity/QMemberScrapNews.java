package com.stockpulse.stockpulseAPI.domain.news.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberScrapNews is a Querydsl query type for MemberScrapNews
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberScrapNews extends EntityPathBase<MemberScrapNews> {

    private static final long serialVersionUID = 911619525L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberScrapNews memberScrapNews = new QMemberScrapNews("memberScrapNews");

    public final com.stockpulse.stockpulseAPI.domain.common.QBaseEntity _super = new com.stockpulse.stockpulseAPI.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.stockpulse.stockpulseAPI.domain.member.entity.QMember member;

    public final QNews news;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMemberScrapNews(String variable) {
        this(MemberScrapNews.class, forVariable(variable), INITS);
    }

    public QMemberScrapNews(Path<? extends MemberScrapNews> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberScrapNews(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberScrapNews(PathMetadata metadata, PathInits inits) {
        this(MemberScrapNews.class, metadata, inits);
    }

    public QMemberScrapNews(Class<? extends MemberScrapNews> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.stockpulse.stockpulseAPI.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.news = inits.isInitialized("news") ? new QNews(forProperty("news")) : null;
    }

}

