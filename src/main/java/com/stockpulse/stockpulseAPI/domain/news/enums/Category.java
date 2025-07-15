package com.stockpulse.stockpulseAPI.domain.news.enums;

public enum Category {
    SEMICONDUCTOR_DISPLAY("반도체디스플레이"),
    SECOND_BATTERY_EV("2차전지전기차"),
    AUTO_PARTS("자동차부품"),
    BIO_PHARMA("바이오제약"),
    ENERGY_RENEWABLE("에너지신재생"),
    STEEL_MATERIALS("철강소재"),
    CONSTRUCTION_REAL_ESTATE("건설부동산"),
    IT_SOFTWARE("IT소프트웨어"),
    GAME_ENTERTAINMENT("게임엔터"),
    TELECOM_5G("통신5G"),
    RETAIL_CONSUMER("유통소비재"),
    FINANCE_INSURANCE("금융보험"),
    TRANSPORT_LOGISTICS("운송물류"),
    UTILITY("유틸리티");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}