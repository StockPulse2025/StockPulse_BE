package com.stockpulse.stockpulseAPI.domain.stock.entity;

import com.stockpulse.stockpulseAPI.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(length = 300)
    private String imageUrl;

    @OneToMany(mappedBy = "stock")
    private List<StockCategory> stockCategories;
}