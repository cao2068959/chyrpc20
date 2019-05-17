package chy.rpc.core.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxyHandle extends AbstractProxy  implements MethodInterceptor {




    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return null;
    }


    @Override
    public Object getPoxy() {
        // 通过CGLIB动态代理获取代理对象的过程
        Enhancer enhancer = new Enhancer();
        // 设置enhancer对象的父类
       // enhancer.setSuperclass(returnClass);
        // 设置enhancer的回调对象
        enhancer.setCallback(this);
        // 创建代理对象
        return  enhancer.create();

    }
}
