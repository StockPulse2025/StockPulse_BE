package com.stockpulse.stockpulseAPI.global.apiPayload.exception.handler;

import com.stockpulse.stockpulseAPI.global.apiPayload.code.BaseErrorCode;
import com.stockpulse.stockpulseAPI.global.apiPayload.exception.GeneralException;

public class MemberHandler extends GeneralException {

    public MemberHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
