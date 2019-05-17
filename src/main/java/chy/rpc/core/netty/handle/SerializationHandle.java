package chy.rpc.core.netty.handle;

import chy.rpc.core.netty.serialization.KryoSevice;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class SerializationHandle extends MessageToByteEncoder {



    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byte[] bytes = KryoSevice.writeObjectToByteArray(o);
        byteBuf.writeBytes(bytes);
        //channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(bytes));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}


