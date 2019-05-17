package chy.rpc.core.proxy;

import chy.rpc.core.bean.RemoteServices;

public class ProxyFactory {

    public static String type_jdk = "jdk";
    public static String type_cglib = "cglib";


    public static Object getProxy(RemoteServices remoteServices,String type){
        AbstractProxy abstractProxy =null;
        if(type_jdk.equals(type)){
            abstractProxy = new JdkProxyHandle(remoteServices);
        }

        if(type_cglib.equals(type)){

        }
        if(abstractProxy == null){
            System.err.println("获取代理对象失败,请正确选择类型");
            return null;
        }
        return abstractProxy.getPoxy();
    }

}
