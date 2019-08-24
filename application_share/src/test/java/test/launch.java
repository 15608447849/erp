package test;

import Ice.InitializationData;
import Ice.Util;
import framework.client.IceClient;
import framework.iceabs.IceLog4jLogger;
import jdbc.imp.TomcatJDBC;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: leeping
 * @Date: 2019/8/5 15:48
 */
public class launch {

    public static void main(String[] args) throws Exception{
//        try {
//            TomcatJDBC.initialize("db-global-0.properties");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        IceClient client = new IceClient("ERP","114.116.149.145:5061");
        client.startCommunication();

        final CountDownLatch countDownLatch= new CountDownLatch(10000);
        for (int i=0 ; i<10000;i++){
            int temp = i;
            new Thread(() -> {
                try {
                    client.settingReq(null,"globalServer","TestModuleI","currentUser")
                            .setArrayParams(
                                    10,
                                    "李世平测试",
                                    "lzp123456",
                                    2
                            );
                    String res = client.execute();
                    System.out.println(temp+" >>> "+res);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        System.out.println("OK");
        client.stopCommunication();
    }


}
