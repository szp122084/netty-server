package com.protobuf.util;

import com.protobuf.pojo.ServiceStatus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlUtil {

    private static Connection conn = Conn.getMySqlConn();


    /**
     * 检查服务是否存在数据库中
     * @param serviceId
     * @return
     */
    public static boolean checkService(int serviceId){
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery("select 1 from service_status where id = "+serviceId);

            if(rs.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(rs,statement);
        }
        return false;
    }

    /**
     * 查询单条服务数据
     * @param sql
     * @return
     */
    public static ServiceStatus selectSingle(String sql){
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);

            while (rs.next()){
                ServiceStatus ss = new ServiceStatus(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5));
                return ss;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(rs,statement);
        }
        System.out.println("未查询到数据");
        return null;
    }

    /**
     * 执行指定sql语句（修改）
     * @param sql
     */
    public static void execSQL(String sql){
        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(statement);
        }
    }

    public static void main(String[] args) throws SQLException {
//        String sql = "select * from service_status where id = 1001";
//
//        ServiceStatus ss = selectSingle(sql);
//        System.out.println(ss);
//
//        String sql1 = "update service_status set online=0 where id = 1001";
//
//        execSQL(sql1);

        String sql = "insert into service_status(id,name,ip,start_time,online) values (" +
                1003 + "," +
                "'test'," +
                "'10.1.50.229'," +
                "NOW()," +
                1 +
                ")";
        System.out.println(sql);
        SqlUtil.execSQL(sql);


    }

    /**
     * 批量关闭流
     * @param closeableList
     */
    public static void close(AutoCloseable... closeableList) {
        for (AutoCloseable closeable : closeableList) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
