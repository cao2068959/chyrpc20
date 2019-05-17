package chy.rpc.core.bean;

public class RpcInvokeBean {

    //要调用的类的名称
    private String serviceName ;

    //要调用的方法的名称
    private String methodName;

    //方法的参数
    private Object[] methodAges;

    //参数类型
    private Class<?>[] paramType;




    public Class<?>[] getParamType() {
        return paramType;
    }

    public void setParamType(Class<?>[] paramType) {
        this.paramType = paramType;
    }


    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getMethodAges() {
        return methodAges;
    }

    public void setMethodAges(Object[] methodAges) {
        this.methodAges = methodAges;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
