package chy.rpc.core.bean;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class RemoteServices {

    //远程服务的IP地址
    private List<String> address;

    //服务的名称
    private String name;

    private Class serviceClass;


    //负载均衡到了哪一个位置
    private int balaceIndex;

    public RemoteServices(List<String> address, String name) {
        this.address = address;
        this.name = name;
        this.balaceIndex = balaceIndex;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalaceIndex() {
        return balaceIndex;
    }

    public void setBalaceIndex(int balaceIndex) {
        this.balaceIndex = balaceIndex;
    }

    public Class getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(Class serviceClass) {
        this.serviceClass = serviceClass;
    }

    public InetSocketAddress getAddressByBalance(){
        if(address == null || address.size() == 0){
            return null;
        }
        String addressStr = null;
        synchronized (this){
            balaceIndex = balaceIndex+1;
            if(balaceIndex > address.size()-1){
                balaceIndex = 0;
            }
            addressStr = address.get(balaceIndex);
        }

        if(addressStr == null){
            return null;
        }

        String[] split = addressStr.split("-");

        InetSocketAddress result = InetSocketAddress.createUnresolved(split[0],Integer.parseInt(split[1]));
        return result;
    }



    public synchronized void addRemoteAddress(String newaddress){
        if(address.contains(newaddress)){
            return;
        }
        address.add(newaddress);
    }


    public synchronized void removeRemoteAddress(String newaddress){
         address.remove(newaddress);
    }
}
