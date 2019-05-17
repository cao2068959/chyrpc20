package chy.test;

import chy.rpc.core.ChyRpcApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    public static void main(String[] args) throws Exception {
        ChyRpcApplication chyRpcApplication = new ChyRpcApplication();
        chyRpcApplication.setPort(7777);
        {
            chyRpcApplication.register("woshidamow",new UserServiceImp());
            chyRpcApplication.register("eeee",new UserServiceImp());


            UserService service = chyRpcApplication.getService("woshidamow",UserService.class);
            UserService service2 = chyRpcApplication.getService("eeee",UserService.class);


            ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);
            for (int i = 0; i <20 ; i++) {
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int j = 0; j <300 ; j++) {
                            System.out.println( "getuser:"+service.getUser(1));
                            System.out.println( "xxx: "+service2.xxx());
                        }
                    }
                });

            }

        }

    }
}
