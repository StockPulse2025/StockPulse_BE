package com.stockpulse.stockpulseAPI.domain.post.entity;

import com.stockpulse.stockpulseAPI.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private Long buy;
    private Long sell;
    private Long hold;
    private Long total;

    @PrePersist
    public void prePersist() {
        this.buy = 0L;
        this.sell = 0L;
        this.hold = 0L;
        this.total = 0L;
    }
}