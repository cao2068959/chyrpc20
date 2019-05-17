package chy.rpc.core.proxy;

import chy.rpc.core.bean.RemoteServices;
import chy.rpc.core.bean.RpcInvokeBean;
import chy.rpc.core.netty.NettyPpcClient;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public abstract class AbstractProxy {

    public abstract Object getPoxy();

    /**
     * PRC客户端真正的调用执行
     * @param remoteServices
     * @param method
     * @param args
     */
    public Object doRPCHandle(RemoteServices remoteServices, Method method, Object[] args) throws Exception {

        //使用 netty访问
        NettyPpcClient nettyPpcClient = NettyPpcClient.getInstance();
        InetSocketAddress addressByBalance = remoteServices.getAddressByBalance();
        if(addressByBalance == null){
            throw  new Exception("没有合适远程服务地址");
        }


        RpcInvokeBean rpcInvokeBean = new RpcInvokeBean();
        rpcInvokeBean.setParamType(method.getParameterTypes());
        rpcInvokeBean.setServiceName(remoteServices.getName());
        rpcInvokeBean.setMethodName(method.getName());
        rpcInvokeBean.setMethodAges(args);
        return nettyPpcClient.connectService(addressByBalance,rpcInvokeBean);

    }
}
