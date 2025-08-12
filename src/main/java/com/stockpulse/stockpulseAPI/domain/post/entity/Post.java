package com.stockpulse.stockpulseAPI.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.stockpulse.stockpulseAPI.domain.common.BaseEntity;
import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(length = 2048, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Stock stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @OneToOne(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Vote vote;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // 조회 성능을 높이기 위해 voteCount, commentCount 추가
    @Column(nullable = true)
    private Integer voteCount = Integer.valueOf(0);

    @Column(nullable = false)
    private Integer commentCount = Integer.valueOf(0);

    @PrePersist
    public void prePersist() {
        if (this.voteCount == null) this.voteCount = 0;
        if (this.commentCount == null) this.commentCount = 0;
    }
}