package com.stockpulse.stockpulseAPI.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketIndexResponse {

    private String contentType;
    private String trId;
    private String trCont;
    private String gtUid;

    @JsonProperty("rt_cd")
    private String rtCd;
    @JsonProperty("msg_cd")
    private String msgCd;
    private String msg1;

    private MarketIndexData output;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarketIndexData {
        @JsonProperty("bstp_nmix_prpr")
        private String bstpNmixPrpr;

        @JsonProperty("bstp_nmix_prdy_vrss")
        private String bstpNmixPrdyVrss;

        @JsonProperty("prdy_vrss_sign")
        private String prdyVrssSign;

        @JsonProperty("bstp_nmix_prdy_ctrt")
        private String bstpNmixPrdyCtrt;

        @JsonProperty("acml_vol")
        private String acmlVol;

        @JsonProperty("prdy_vol")
        private String prdyVol;

        @JsonProperty("acml_tr_pbmn")
        private String acmlTrPbmn;

        @JsonProperty("prdy_tr_pbmn")
        private String prdyTrPbmn;

        @JsonProperty("bstp_nmix_oprc")
        private String bstpNmixOprc;

        @JsonProperty("prdy_nmix_vrss_nmix_oprc")
        private String prdyNmixVrssNmixOprc;

        @JsonProperty("oprc_vrss_prpr_sign")
        private String oprcVrssPrprSign;

        @JsonProperty("bstp_nmix_oprc_prdy_ctrt")
        private String bstpNmixOprcPrdyCtrt;

        @JsonProperty("bstp_nmix_hgpr")
        private String bstpNmixHgpr;

        @JsonProperty("prdy_nmix_vrss_nmix_hgpr")
        private String prdyNmixVrssNmixHgpr;

        @JsonProperty("hgpr_vrss_prpr_sign")
        private String hgprVrssPrprSign;

        @JsonProperty("bstp_nmix_hgpr_prdy_ctrt")
        private String bstpNmixHgprPrdyCtrt;

        @JsonProperty("bstp_nmix_lwpr")
        private String bstpNmixLwpr;

        @JsonProperty("prdy_clpr_vrss_lwpr")
        private String prdyClprVrssLwpr;

        @JsonProperty("lwpr_vrss_prpr_sign")
        private String lwprVrssPrprSign;

        @JsonProperty("prdy_clpr_vrss_lwpr_rate")
        private String prdyClprVrssLwprRate;

        @JsonProperty("ascn_issu_cnt")
        private String ascnIssuCnt;

        @JsonProperty("uplm_issu_cnt")
        private String uplmIssuCnt;

        @JsonProperty("stnr_issu_cnt")
        private String stnrIssuCnt;

        @JsonProperty("down_issu_cnt")
        private String downIssuCnt;

        @JsonProperty("lslm_issu_cnt")
        private String lslmIssuCnt;

        @JsonProperty("dryy_bstp_nmix_hgpr")
        private String dryyBstpNmixHgpr;

        @JsonProperty("dryy_hgpr_vrss_prpr_rate")
        private String dryyHgprVrssPrprRate;

        @JsonProperty("dryy_bstp_nmix_hgpr_date")
        private String dryyBstpNmixHgprDate;

        @JsonProperty("dryy_bstp_nmix_lwpr")
        private String dryyBstpNmixLwpr;

        @JsonProperty("dryy_lwpr_vrss_prpr_rate")
        private String dryyLwprVrssPrprRate;

        @JsonProperty("dryy_bstp_nmix_lwpr_date")
        private String dryyBstpNmixLwprDate;

        @JsonProperty("total_askp_rsqn")
        private String totalAskpRsqn;

        @JsonProperty("total_bidp_rsqn")
        private String totalBidpRsqn;

        @JsonProperty("seln_rsqn_rate")
        private String selnRsqnRate;

        @JsonProperty("shnu_rsqn_rate")
        private String shnuRsqnRate;

        @JsonProperty("ntby_rsqn")
        private String ntbyRsqn;
    }
}
