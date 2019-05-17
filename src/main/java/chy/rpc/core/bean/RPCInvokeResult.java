package chy.rpc.core.bean;

public class RPCInvokeResult {

    private boolean success;

    private Object data;

    private String msg;

    public boolean isSuccess() {
        return success;
    }

    public RPCInvokeResult() {
    }

    public RPCInvokeResult(boolean success, Object data, String msg) {
        this.success = success;
        this.data = data;
        this.msg = msg;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static RPCInvokeResult fail(String msg){
        return new RPCInvokeResult(false,null,msg);
    }

    public static RPCInvokeResult success(Object data){
        return new RPCInvokeResult(true,data,null);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
