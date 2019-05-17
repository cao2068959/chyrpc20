package chy.rpc.core.netty.handle;

import chy.rpc.core.bean.RPCInvokeResult;
import chy.rpc.core.bean.RemoteServices;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.CountDownLatch;

public class RPCClientChannel extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext channelHandlerContext;

    private RPCInvokeResult rpcInvokeResult;

    private CountDownLatch countDownLatch;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        rpcInvokeResult = (RPCInvokeResult) msg;
        if(countDownLatch != null){
            countDownLatch.countDown();
        }
       // ctx.channel().close();
       // System.out.println("############3");
    }


    public Object getData() throws Exception {
        if(countDownLatch == null){
            throw new Exception("获取数据异常");
        }
        //等待拿数据
        countDownLatch.await();
        if(!rpcInvokeResult.isSuccess()){
            throw new Exception(rpcInvokeResult.getMsg());
        }
        return rpcInvokeResult.getData();
    }

    public void startCount(){
        countDownLatch = new CountDownLatch(1);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //拿完数据关闭
       // System.out.println("---------------");
       // ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
