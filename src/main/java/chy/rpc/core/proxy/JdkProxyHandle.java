package chy.rpc.core.proxy;

import chy.rpc.core.bean.RemoteServices;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JdkProxyHandle extends AbstractProxy implements InvocationHandler {


    public RemoteServices remoteServices;

    public JdkProxyHandle(RemoteServices remoteServices) {
        this.remoteServices = remoteServices;
    }

    public Object getPoxy(){
        Class interfaceClass = remoteServices.getServiceClass();
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(),
               new Class[]{interfaceClass},this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //如果调用object默认方法,就放行
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }


        //调用模版方法,来获取参数
        Object result = doRPCHandle(remoteServices, method, args);
        return result;
    }
}
