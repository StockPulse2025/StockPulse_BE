package com.stockpulse.stockpulseAPI.domain.stock.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberOwnStock is a Querydsl query type for MemberOwnStock
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberOwnStock extends EntityPathBase<MemberOwnStock> {

    private static final long serialVersionUID = -49536952L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberOwnStock memberOwnStock = new QMemberOwnStock("memberOwnStock");

    public final com.stockpulse.stockpulseAPI.domain.common.QBaseEntity _super = new com.stockpulse.stockpulseAPI.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.stockpulse.stockpulseAPI.domain.member.entity.QMember member;

    public final QStock stock;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMemberOwnStock(String variable) {
        this(MemberOwnStock.class, forVariable(variable), INITS);
    }

    public QMemberOwnStock(Path<? extends MemberOwnStock> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberOwnStock(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberOwnStock(PathMetadata metadata, PathInits inits) {
        this(MemberOwnStock.class, metadata, inits);
    }

    public QMemberOwnStock(Class<? extends MemberOwnStock> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.stockpulse.stockpulseAPI.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.stock = inits.isInitialized("stock") ? new QStock(forProperty("stock")) : null;
    }

}

