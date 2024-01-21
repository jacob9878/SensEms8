package com.imoxion.sensems.server.nio.smtp;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ImSmtpRespMsg {
    private final String traceId;
    private final String respMessage;
}
