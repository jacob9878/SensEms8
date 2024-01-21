package com.imoxion.sensems.server.nio.codec;

import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ProtocolDetectionResult;
import io.netty.handler.codec.ProtocolDetectionState;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.codec.haproxy.HAProxyProtocolVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImHAProxyMessageDecoder extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(ImHAProxyMessageDecoder.class);

    public ImHAProxyMessageDecoder(String loggerName) {
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof ByteBuf) {
            try {
                logger.trace("ImHAProxyMessageDecoder.channelRead");
//                logger.info( "ImHAProxyMessageDecoder 0000 - " + ((ByteBuf) msg).toString(Charset.forName("utf-8")) );

                ProtocolDetectionResult<HAProxyProtocolVersion> result = HAProxyMessageDecoder.detectProtocol((ByteBuf) msg);

                if (result.state() == ProtocolDetectionState.DETECTED) {
                    // haproxydecoder 뒤에 실제 HAProxyMessageDecoder를 붙인다. 한번 사용 후 제거
                    ctx.pipeline().addAfter("haproxydecoder", null, new HAProxyMessageDecoder());

                    // 한번 사용 후 제거
                    //ctx.pipeline().remove(this);
                }
                logger.trace("ImHAProxyMessageDecoder.channelRead 2");
            } catch (Exception e) {
                if (msg != null) {
                    try { if (((ByteBuf) msg).refCnt() > 0) ((ByteBuf) msg).release(); } catch (Exception ex) {}
                }
                String errorId = ErrorTraceLogger.log(e);
                logger.error("[{}] ImHAProxyMessageDecoder.channelRead error", errorId);
            } finally {
                // 한번 사용 후 제거
                ctx.pipeline().remove(this);
            }
        }

        // 이거 빼먹으면 큰일남
        super.channelRead(ctx, msg);
    }
}
