package chy.rpc.core.netty;

import chy.rpc.core.bean.RpcInvokeBean;
import chy.rpc.core.netty.handle.RPCClientChannel;
import chy.rpc.core.netty.handle.SerializationHandle;
import chy.rpc.core.netty.handle.UnSerializationHandle;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.net.InetSocketAddress;

public class NettyPpcClient {

    private int pageLen = 1024*1024*10;
    //private Bootstrap bootstrap;

    EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private NettyPpcClient() {

        //initNetty();
    }

    private ChannelFuture initNetty(InetSocketAddress inetSocketAddress,EventLoopGroup eventLoopGroup){

        Bootstrap bootstrap = new Bootstrap();
        SerializationHandle serializationHandle = new SerializationHandle();
        UnSerializationHandle unSerializationHandle = new UnSerializationHandle();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new LengthFieldPrepender(4));
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(pageLen,0,4,0,4));
                        pipeline.addLast(new SerializationHandle());
                        pipeline.addLast(new UnSerializationHandle());
                        pipeline.addLast("clientHandle",new RPCClientChannel());
                    }
                });

        ChannelFuture connect = bootstrap.connect(inetSocketAddress);
        try {
            connect.sync();
            return connect;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }


    public Object connectService(InetSocketAddress inetSocketAddress, RpcInvokeBean rpcInvokeBean) throws Exception {


        ChannelFuture connect = initNetty(inetSocketAddress,eventLoopGroup);

        try{

            RPCClientChannel clientHandle = (RPCClientChannel) connect.channel().pipeline().get("clientHandle");
            clientHandle.startCount();
            //发送数据
            connect.channel().writeAndFlush(rpcInvokeBean).sync();
            //等待获取数据
            Object data = clientHandle.getData();

            return data;

        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            connect.channel().close().sync();

        }

    }

    /**
     * 单列获取对象
     * @return
     */
    public static NettyPpcClient getInstance(){
        return Instance.nettyPpcClient;
    }

    static class Instance{
        public static NettyPpcClient nettyPpcClient = new NettyPpcClient();
    }




}
