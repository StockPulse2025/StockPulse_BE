package com.stockpulse.stockpulseAPI.domain.stock.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberFavoriteStock is a Querydsl query type for MemberFavoriteStock
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberFavoriteStock extends EntityPathBase<MemberFavoriteStock> {

    private static final long serialVersionUID = -1715975998L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberFavoriteStock memberFavoriteStock = new QMemberFavoriteStock("memberFavoriteStock");

    public final com.stockpulse.stockpulseAPI.domain.common.QBaseEntity _super = new com.stockpulse.stockpulseAPI.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.stockpulse.stockpulseAPI.domain.member.entity.QMember member;

    public final QStock stock;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMemberFavoriteStock(String variable) {
        this(MemberFavoriteStock.class, forVariable(variable), INITS);
    }

    public QMemberFavoriteStock(Path<? extends MemberFavoriteStock> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberFavoriteStock(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberFavoriteStock(PathMetadata metadata, PathInits inits) {
        this(MemberFavoriteStock.class, metadata, inits);
    }

    public QMemberFavoriteStock(Class<? extends MemberFavoriteStock> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.stockpulse.stockpulseAPI.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.stock = inits.isInitialized("stock") ? new QStock(forProperty("stock")) : null;
    }

}

