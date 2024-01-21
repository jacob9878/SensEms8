package com.imoxion.sensems.server.nio.smtp.initializer;

import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.nio.codec.ImHAProxyMessageDecoder;
import com.imoxion.sensems.server.nio.codec.ImStringDecoder;
import com.imoxion.sensems.server.nio.smtp.handler.ImSmtpIspServerHandler;
import com.imoxion.sensems.server.smtp.ImSmtpSession;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ImSmtpIspInitializier extends ChannelInitializer<SocketChannel> {
    private final SslContext sslContext;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast("idlehandler", new IdleStateHandler(0, 0, ImSmtpConfig.getInstance().getRWTime(), TimeUnit.MILLISECONDS));
        cp.addLast(new ReadTimeoutHandler(ImSmtpConfig.getInstance().getRWTime()/1000));
//        cp.addLast(new WriteTimeoutHandler(ImSmtpConfig.getInstance().getRWTime()/1000));
        cp.addLast("line", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()));
//        cp.addLast("line", new LineBasedFrameDecoder(Integer.MAX_VALUE));
        cp.addLast("decoder", new ImStringDecoder());
        cp.addLast("encoder", new StringEncoder());
        cp.addLast("basehandler", new ImSmtpIspServerHandler(new ImSmtpSession(sslContext)));

        // proxy를 거쳐서 오는 경우 클라이언트 아이피 추출을 위해서 처리
        // PROXY TCP4/TCP6 출발지IP 프록시서버IP 출발지(proxy)포트 목적지포트
        cp.addFirst("haproxydecoder", new ImHAProxyMessageDecoder("SMTP"));
    }
}
