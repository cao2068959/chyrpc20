package chy.test;

import chy.rpc.core.ChyRpcApplication;
import chy.test.bean.Query;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    public static void main(String[] args) throws Exception {
        ChyRpcApplication chyRpcApplication = new ChyRpcApplication();
        chyRpcApplication.setPort(7778);
        chyRpcApplication.setIp("127.0.0.1");
        chyRpcApplication.setZookeeperAddress("127.0.0.1:2181");
        {
            chyRpcApplication.register("chy.test.UserService", new UserServiceImp());
            chyRpcApplication.register("noInterface", new NoInterfaceImg());
            UserService service = chyRpcApplication.getService(UserService.class);
            INoInterface iNoInterface = chyRpcApplication.getService("noInterface",INoInterface.class);
            System.out.println(service.getUser(10086));
            System.out.println(iNoInterface.exec("没有接口呀"));

            Query query = new Query("小明",99);
            System.out.println(service.getUser(query));



        }

    }
}
