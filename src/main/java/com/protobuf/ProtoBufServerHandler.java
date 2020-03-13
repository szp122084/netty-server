package com.protobuf;

import com.protobuf.pojo.Global;
import com.protobuf.pojo.ProtoBuf;
import com.protobuf.util.SqlUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentHashMap;

public class ProtoBufServerHandler extends SimpleChannelInboundHandler<ProtoBuf.Message>{

    /**
     * 客户端通道集合
     */
    public static ConcurrentHashMap<Integer, ChannelHandlerContext> clientMap = new ConcurrentHashMap<>();

    private int clientId = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoBuf.Message msg) throws Exception {
        int type = msg.getType();

        if(type == Global.HEART){//客户端心跳检测

            System.out.println(Thread.currentThread()+"=="+clientId+"心跳包，不处理，直接返回");
            sendMsgToClient(ctx,msg);

        }else if (type == Global.REGISTER){//客户端注册事件

            //1、将服务id标识到线程中
            clientId = msg.getClientId();
            System.out.println(Thread.currentThread()+"=="+msg.getClientId()+"初始化，服务ID："+clientId+",服务名称："+msg.getMsg());
            //2、加入在线列表
            clientMap.put(clientId,ctx);
            //3、与数据库交互
            if(!SqlUtil.checkService(clientId)){//不存在，加入

                String serviceInfo = msg.getMsg();
                String[] info = serviceInfo.split("#==#");

                SqlUtil.execSQL("insert into service_status(id,name,ip,start_time,online) values (" +
                        clientId + "," + "'"+ info[0] +"'," + "'" + info[1] +"'," + "NOW()," + 1 + ")");
            }else{//已存在，设置在线
                SqlUtil.execSQL("update service_status set online = 1 where id = "+clientId);
            }

        }else if (type == Global.QUIT){//客户端主动下线

            System.out.println(Thread.currentThread()+"=="+msg.getClientId()+"发来关闭连接请求");
            closeClient(ctx);

        }else{
            System.out.println(Thread.currentThread()+"=="+msg.getClientId()+"其他消息，类型:"+type+",数据："+msg.getMsg());
        }



    }

    /**
     * 发送消息到客户端
     * @param ctx
     * @param msg
     */
    public void sendMsgToClient(ChannelHandlerContext ctx, ProtoBuf.Message msg){
        ctx.channel().writeAndFlush(msg);
    }

    /**
     * 关闭客户端连接
     * @param ctx
     */
    public void closeClient(ChannelHandlerContext ctx){
        //1、移出在线列表
        clientMap.remove(clientId);
        //2、关闭连接
        ctx.channel().close();

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        System.out.println("触发读超时，主动发送关闭信号给客户端（无论是否能收到）");

       //告诉客户端关闭连接
        ProtoBuf.Message closeMsg = ProtoBuf.Message.newBuilder().setType(4).build();
        ctx.writeAndFlush(closeMsg);

        //关闭该连接
        closeClient(ctx);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("抓到异常："+cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //channel失效处理,客户端下线或者强制退出等任何情况都触发这个方法
        //3、更新数据库状态
        System.out.println("客户端[" + clientId + "]断开连接，更新数据库状态");
        SqlUtil.execSQL("update service_status set online = 0,stop_time = NOW() where id = "+clientId);
    }
}