package chy.rpc.core;

import chy.rpc.core.bean.RPCInvokeResult;
import chy.rpc.core.bean.RpcInvokeBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 *  RPC服务正真的执行者
 */
public class ChyRpcSeviceInvok {

    //本地服务的缓存,消费者将来这里执行方法
    public Map<String,Object> serviceCache = new HashMap<>();


    public void addService(String name, Object seviceInstance) {


        //name = name.replaceAll("/","\\.");
        Object iscache = serviceCache.get(name);
        if(iscache != null){
            return;
        }
        serviceCache.put(name,seviceInstance);
    }

    public  static ChyRpcSeviceInvok getInstance(){
        return Instance.chyRpcSeviceInvok;
    }

    public RPCInvokeResult invoke(String serviceName, String methodName, Object[] methodAges, Class<?>[] paramType) {
        if(serviceName == null || methodName == null){
            return RPCInvokeResult.fail("参数异常");
        }


        Object execInstance = serviceCache.get(serviceName);
        if(execInstance == null){
            return RPCInvokeResult.fail("没有提供对应的服务");
        }
        Class<?> execInstanceClass = execInstance.getClass();


        try {
            Method method = execInstanceClass.getMethod(methodName, paramType);
            Object result = method.invoke(execInstance, methodAges);
            return RPCInvokeResult.success(result);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return RPCInvokeResult.fail("没有提供对应的方法");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return RPCInvokeResult.fail("远程服务器方法参数异常");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return RPCInvokeResult.fail("远程服务方法调用失败");
        }


    }

    static class Instance{
        public static  ChyRpcSeviceInvok chyRpcSeviceInvok = new ChyRpcSeviceInvok();
    }



}
