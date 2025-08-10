package com.stockpulse.stockpulseAPI.domain.notification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotificationSetting is a Querydsl query type for NotificationSetting
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotificationSetting extends EntityPathBase<NotificationSetting> {

    private static final long serialVersionUID = 1010978376L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotificationSetting notificationSetting = new QNotificationSetting("notificationSetting");

    public final com.stockpulse.stockpulseAPI.domain.common.QBaseEntity _super = new com.stockpulse.stockpulseAPI.domain.common.QBaseEntity(this);

    public final BooleanPath badNews = createBoolean("badNews");

    public final NumberPath<java.math.BigDecimal> badSensitivity1 = createNumber("badSensitivity1", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> badSensitivity2 = createNumber("badSensitivity2", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final BooleanPath goodNews = createBoolean("goodNews");

    public final NumberPath<java.math.BigDecimal> goodSensitivity1 = createNumber("goodSensitivity1", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> goodSensitivity2 = createNumber("goodSensitivity2", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath interestStock = createBoolean("interestStock");

    public final com.stockpulse.stockpulseAPI.domain.member.entity.QMember member;

    public final BooleanPath neutralNews = createBoolean("neutralNews");

    public final BooleanPath ownStock = createBoolean("ownStock");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QNotificationSetting(String variable) {
        this(NotificationSetting.class, forVariable(variable), INITS);
    }

    public QNotificationSetting(Path<? extends NotificationSetting> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotificationSetting(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotificationSetting(PathMetadata metadata, PathInits inits) {
        this(NotificationSetting.class, metadata, inits);
    }

    public QNotificationSetting(Class<? extends NotificationSetting> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.stockpulse.stockpulseAPI.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

