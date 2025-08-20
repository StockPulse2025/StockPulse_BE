package com.stockpulse.stockpulseAPI.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class StockHistoryResponse {
    @JsonProperty("output2")
    private List<StockHistoryDto> output2;

    @Data
    public static class StockHistoryDto {
        @JsonProperty("stck_bsop_date")
        private String stckBsopDate;

        @JsonProperty("stck_clpr")
        private String stckClpr;

        @JsonProperty("stck_oprc")
        private String stckOprc;

        @JsonProperty("stck_hgpr")
        private String stckHgpr;

        @JsonProperty("stck_lwpr")
        private String stckLwpr;

        @JsonProperty("acml_vol")
        private String acmlVol;

        @JsonProperty("acml_tr_pbmn")
        private String acmlTrPbmn;

        @JsonProperty("flng_cls_code")
        private String flngClsCode;

        @JsonProperty("prtt_rate")
        private String prttRate;

        @JsonProperty("mod_yn")
        private String modYn;

        @JsonProperty("prdy_vrss_sign")
        private String prdyVrssSign;

        @JsonProperty("prdy_vrss")
        private String prdyVrss;

        @JsonProperty("revl_issu_reas")
        private String revlIssuReas;
    }
}
