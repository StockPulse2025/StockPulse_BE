package com.stockpulse.stockpulseAPI.domain.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QVoteItem is a Querydsl query type for VoteItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVoteItem extends EntityPathBase<VoteItem> {

    private static final long serialVersionUID = 30672975L;

    public static final QVoteItem voteItem = new QVoteItem("voteItem");

    public final com.stockpulse.stockpulseAPI.domain.common.QBaseEntity _super = new com.stockpulse.stockpulseAPI.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final EnumPath<com.stockpulse.stockpulseAPI.domain.post.entity.enums.VoteOption> voteOption = createEnum("voteOption", com.stockpulse.stockpulseAPI.domain.post.entity.enums.VoteOption.class);

    public QVoteItem(String variable) {
        super(VoteItem.class, forVariable(variable));
    }

    public QVoteItem(Path<? extends VoteItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVoteItem(PathMetadata metadata) {
        super(VoteItem.class, metadata);
    }

}

