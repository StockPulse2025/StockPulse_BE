package com.stockpulse.stockpulseAPI.domain.news.exception;

import com.stockpulse.stockpulseAPI.global.apiPayload.code.BaseErrorCode;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.GeneralException;

public class NewsException extends GeneralException {
    public NewsException(BaseErrorCode code) {
        super(code);
    }
}
