package chy.rpc.core.netty.handle;

import chy.rpc.core.ChyRpcSeviceInvok;
import chy.rpc.core.bean.RPCInvokeResult;
import chy.rpc.core.bean.RpcInvokeBean;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

public class RrcServiceChannel extends ChannelInboundHandlerAdapter {



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcInvokeBean rpcInvokeBean = (RpcInvokeBean)msg;
        String serviceName = rpcInvokeBean.getServiceName();
        String methodName = rpcInvokeBean.getMethodName();
        Object[] methodAges = rpcInvokeBean.getMethodAges();
        Class<?>[] paramType = rpcInvokeBean.getParamType();
        //执行远程方法
        RPCInvokeResult execResult = ChyRpcSeviceInvok.getInstance().invoke(serviceName, methodName, methodAges, paramType);
        //把结果写回去
        ctx.channel().writeAndFlush(execResult);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

}
