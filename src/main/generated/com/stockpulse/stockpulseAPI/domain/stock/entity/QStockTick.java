package com.stockpulse.stockpulseAPI.domain.stock.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStockTick is a Querydsl query type for StockTick
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStockTick extends EntityPathBase<StockTick> {

    private static final long serialVersionUID = -1460228427L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStockTick stockTick = new QStockTick("stockTick");

    public final NumberPath<java.math.BigDecimal> changeAmount = createNumber("changeAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> changeRate = createNumber("changeRate", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> closePrice = createNumber("closePrice", java.math.BigDecimal.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<java.math.BigDecimal> highPrice = createNumber("highPrice", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<java.math.BigDecimal> lowPrice = createNumber("lowPrice", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> openPrice = createNumber("openPrice", java.math.BigDecimal.class);

    public final QStock stock;

    public final NumberPath<Long> tradingValue = createNumber("tradingValue", Long.class);

    public final NumberPath<Long> tradingVolume = createNumber("tradingVolume", Long.class);

    public QStockTick(String variable) {
        this(StockTick.class, forVariable(variable), INITS);
    }

    public QStockTick(Path<? extends StockTick> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStockTick(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStockTick(PathMetadata metadata, PathInits inits) {
        this(StockTick.class, metadata, inits);
    }

    public QStockTick(Class<? extends StockTick> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.stock = inits.isInitialized("stock") ? new QStock(forProperty("stock")) : null;
    }

}

