package com.imoxion.sensems.server.nio.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class ImStringDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final byte[] byteCRLF = "\r\n".getBytes();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] data = null;
        //byte[] byteCRLF = "\r\n".getBytes();
        if (byteBuf.isReadable()){
            data = ByteBufUtil.getBytes(byteBuf);
        }

        //System.out.println("  ImStringDecoder " + new String(data));
        if(data != null) {
            list.add(data);
        } else {
            list.add(byteCRLF);
        }
    }
}
