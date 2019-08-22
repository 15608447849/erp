package test;

import framework.client.IceClient;

/**
 * @Author: leeping
 * @Date: 2019/8/5 15:48
 */
public class launch {
    public static void main(String[] args) {
        IceClient client = new IceClient("DemoIceGrid","192.168.1.145:4061");
        client.startCommunication();
        for (int i = 0 ; i<10000 ;i++){
            client.setServerAndRequest("globalServer","TestModule","callback")
                    .setArrayParams("测试数据");
            String res = client.execute();
            System.out.println(i+"->" +res);
        }

        client.stopCommunication();

    }
}
