package com.stockpulse.stockpulseAPI.domain.news.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QImpact is a Querydsl query type for Impact
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QImpact extends EntityPathBase<Impact> {

    private static final long serialVersionUID = 2088061419L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QImpact impact = new QImpact("impact");

    public final com.stockpulse.stockpulseAPI.domain.common.QBaseEntity _super = new com.stockpulse.stockpulseAPI.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<java.math.BigDecimal> impactRate = createNumber("impactRate", java.math.BigDecimal.class);

    public final QNews news;

    public final com.stockpulse.stockpulseAPI.domain.stock.entity.QStock stock;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QImpact(String variable) {
        this(Impact.class, forVariable(variable), INITS);
    }

    public QImpact(Path<? extends Impact> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QImpact(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QImpact(PathMetadata metadata, PathInits inits) {
        this(Impact.class, metadata, inits);
    }

    public QImpact(Class<? extends Impact> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.news = inits.isInitialized("news") ? new QNews(forProperty("news")) : null;
        this.stock = inits.isInitialized("stock") ? new com.stockpulse.stockpulseAPI.domain.stock.entity.QStock(forProperty("stock")) : null;
    }

}

