package com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler;

import com.stockpulse.stockpulseAPI.global.apiPayload.code.BaseErrorCode;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.GeneralException;

public class NewsHandler extends GeneralException {

    public NewsHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
