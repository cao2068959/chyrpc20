package chy.rpc.core;

import chy.rpc.core.bean.RemoteServices;
import chy.rpc.core.netty.NettyRpcService;
import chy.rpc.core.proxy.ProxyFactory;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import lombok.Getter;
import lombok.Setter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChyRpcApplication {

    @Setter
    private String root = "chyrpc";

    @Setter
    private String secRoot = "/RPCsevice";

    /**
     * zk的地址,集群里可以用 逗号分隔
     */
    @Setter
    private String zookeeperAddress = "127.0.0.1:2181";

    /**
     * PRC远程端口
     */
    @Getter
    @Setter
    private int port;

    /**
     * 提供者的ip地址,消费者将会通过这个ip和提供者消费服务
     */
    @Getter
    @Setter
    private String ip = "127.0.0.1";

    private CuratorFramework curatorFramework;
    //远程服务的缓存
    private Map<String, RemoteServices> serviceCache = new ConcurrentHashMap<>();

    private TreeCache treeCache;


    //netty服务,如果这边有对象注册,则开启netty服务
    private NettyRpcService nettyRpcService;

    public ChyRpcApplication() {
        initZookeeper();
    }

    public ChyRpcApplication(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
        initZookeeper();
    }




    /**
     * 初始化zookeeper连接
     */
    private void initZookeeper(){
        curatorFramework = CuratorFrameworkFactory.builder().namespace(root)
                .connectString(zookeeperAddress)
                .connectionTimeoutMs(3000)
                .sessionTimeoutMs(2000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorFramework.start();
    }


    /**
     * 注册服务
     * @param name
     * @param seviceInstance
     * @throws Exception
     */
    public void register(String name,Object seviceInstance) throws Exception {
        if(ip==null && port == 0){
            throw new Exception("没有指定PRC服务端IP地址及端口");
        }
         String servicePath = name.replaceAll("\\.", "/");
        String node = secRoot+"/"+servicePath + "/" + ip+"-"+port;
        if(curatorFramework.checkExists().forPath(node)!=null){
            //如果 原来的节点还没删除,先删除
            curatorFramework.delete().forPath(node);
        }
        //添加新的节点
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                .forPath(node);

        //开启 netty服务器,等后调用者
        if(nettyRpcService == null){
            nettyRpcService = new NettyRpcService(ip,port);
        }

        ChyRpcSeviceInvok chyRpcSeviceInvok = ChyRpcSeviceInvok.getInstance();
        chyRpcSeviceInvok.addService(name,seviceInstance);
    }


    public void register(Class SeviceInterface,Object seviceInstance) throws Exception {
        register(SeviceInterface.getName(),seviceInstance);

    }


    /**
     * 发现服务
     * @param <T>
     * @return
     */
    public <T> T getService(String name,Class<T> creatClass) throws Exception {
        watchServiceChange();
        RemoteServices remoteService = getSeviceByCacheOrZookeeper(name,creatClass);
        return (T) ProxyFactory.getProxy(remoteService,ProxyFactory.type_jdk);
    }


    public <T> T getService(Class<T> creatClass) throws Exception {
        return getService(creatClass.getName(),creatClass);
    }


    /**
     * 从缓存中获取PRC资源,如果没有从,zookeeper中获取
     * @param name
     * @return
     */
    private RemoteServices getSeviceByCacheOrZookeeper(String name,Class creatClass) throws Exception {



        RemoteServices remoteServices = serviceCache.get(name);
        //拿到缓存数据,直接走人
        if(remoteServices != null){
            return remoteServices;
        }

        String nodePath = secRoot+"/"+name.replaceAll("\\.", "/");;

        //拿不到去zookeeper拿一下
        //先检查有没对应的节点,没有就不拿
        List<String> ipAndports =null;
       if(curatorFramework.checkExists().forPath(nodePath) == null){
           ipAndports = new ArrayList<>();
       }else{
           ipAndports = curatorFramework.getChildren().forPath(nodePath);
       }



        remoteServices = new RemoteServices(ipAndports,name);
        remoteServices.setServiceClass(creatClass);

        serviceCache.put(name,remoteServices);
        return remoteServices;
    }


    /**
     * 监控节点的变动,如果有变动,刷新缓存中 远程服务器的数据
     */
    private void watchServiceChange() throws Exception {
        if(treeCache != null){
            return ;
        }

        treeCache = new TreeCache(curatorFramework,secRoot);
        treeCache.start();
        treeCache.getListenable().addListener((curatorFramework, treeCacheEvent) -> {
            ChildData childData = treeCacheEvent.getData();
            if(childData == null){
                return;
            }
            //获取更变的路径
            String path = childData.getPath();
            //提供者增加
            if(treeCacheEvent.getType() ==TreeCacheEvent.Type.NODE_ADDED){
                //用正则,解析路径信息,最后必须带ip地址才能解析通过,index1 类路径 index2 ip信息
                String[] pathAndIp = pathPattern(path);
                if(pathAndIp == null){
                    return;
                }
                //获取缓存中的远程服务对象
                RemoteServices remoteServices = serviceCache.get(pathAndIp[0]);
                if(remoteServices == null){
                    return;
                }
                //添加进新的ip地址
                remoteServices.addRemoteAddress(pathAndIp[1]);


                //提供者减少
            }else if(treeCacheEvent.getType() ==TreeCacheEvent.Type.NODE_REMOVED){
                String[] pathAndIp = pathPattern(path);
                if(pathAndIp == null){
                    return;
                }
                RemoteServices remoteServices = serviceCache.get( pathAndIp[0]);
                if(remoteServices == null){
                    return;
                }
                //删除对应的Ip地址
                remoteServices.removeRemoteAddress(pathAndIp[1]);

            }

        });


    }




    /**
     * 正则匹配node的地址
     * 没有匹配上返回null
     * 匹配上返回一个数组
     * 1 位是: path
     * 2 位是: 后面的ip地址
     */
    private String[] pathPattern(String data){
        Pattern compile = Pattern.compile("/RPCsevice/(.*)/(.*-[0-9]{3,6})");
        Matcher matcher = compile.matcher(data);
        if(!matcher.find()){
            return null;
        }
        String serviceName = matcher.group(1);
        serviceName = serviceName.replaceAll("/","\\.");

        String[] result = {serviceName,matcher.group(2)} ;
        return result;
    }

    public static void main(String[] args) {
        String a = "/RPCsevice/xxx/ServiceApi/127.0.0.1-9000";
        Pattern compile = Pattern.compile("/RPCsevice(.*)/(.*-[0-9]{3,6})");
        Matcher matcher = compile.matcher(a);
        matcher.find();
        System.out.println(matcher.group(1));
    }

}
