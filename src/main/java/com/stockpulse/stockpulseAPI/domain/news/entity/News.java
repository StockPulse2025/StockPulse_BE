package com.stockpulse.stockpulseAPI.domain.news.entity;

import com.stockpulse.stockpulseAPI.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class News extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 300)
    private String image;

    @Column(nullable = false, length = 300, unique = true)
    private String url;

    @Lob
    private String content;

    @Column(nullable = false)
    private String press;

    private String reason;

    @Column(nullable = false)
    private LocalDateTime publishedDate;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Impact> impacts = new ArrayList<>();
}
