package com.imoxion.sensems.server.nio.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImObjectDecoder extends ObjectDecoder {
    public static Logger logger = LoggerFactory.getLogger("TRANSFER");

    public ImObjectDecoder(ClassResolver classResolver) {
        super(classResolver);
        logger.info("ImObjectDecoder.ImObjectDecoder");
    }

    public ImObjectDecoder(int maxObjectSize, ClassResolver classResolver) {
        super(maxObjectSize, classResolver);
        logger.info("ImObjectDecoder.ImObjectDecoder 2");
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        logger.info("ImObjectDecoder.decode ");
        return super.decode(ctx, in);
    }


}
