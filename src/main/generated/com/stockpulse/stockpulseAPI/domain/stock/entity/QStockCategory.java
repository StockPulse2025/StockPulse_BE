package com.stockpulse.stockpulseAPI.domain.stock.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStockCategory is a Querydsl query type for StockCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStockCategory extends EntityPathBase<StockCategory> {

    private static final long serialVersionUID = 1805569558L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStockCategory stockCategory = new QStockCategory("stockCategory");

    public final com.stockpulse.stockpulseAPI.domain.common.QBaseEntity _super = new com.stockpulse.stockpulseAPI.domain.common.QBaseEntity(this);

    public final QCategory category;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QStock stock;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QStockCategory(String variable) {
        this(StockCategory.class, forVariable(variable), INITS);
    }

    public QStockCategory(Path<? extends StockCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStockCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStockCategory(PathMetadata metadata, PathInits inits) {
        this(StockCategory.class, metadata, inits);
    }

    public QStockCategory(Class<? extends StockCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
        this.stock = inits.isInitialized("stock") ? new QStock(forProperty("stock")) : null;
    }

}

