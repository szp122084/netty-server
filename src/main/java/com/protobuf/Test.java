package com.protobuf;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Test implements Runnable {


    @Override
    public void run() {

            int i = 0;
        while (true){


            ConcurrentHashMap<Integer, ChannelHandlerContext> clientMap = ProtoBufServerHandler.clientMap;
            if(clientMap.size()>i){
            System.out.println(clientMap.size());
                i = clientMap.size();
                for (Map.Entry entry : clientMap.entrySet()){
                    System.out.println("当前在线客户端："+entry.getKey()+"==="+entry.getValue());
                }
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
