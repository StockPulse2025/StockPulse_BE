package com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler;

import com.stockpulse.stockpulseAPI.global.apiPayload.code.BaseErrorCode;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.GeneralException;

public class StockHandler extends GeneralException {

    public StockHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
