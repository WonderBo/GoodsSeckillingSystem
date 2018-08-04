package com.cqu.wb.util;

/**
 * Created by jingquan on 2018/8/3.
 */

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * @description Spring-boot自动管理mvc使用的数据库连接，其他地方使用数据库连接需要自己手动生成管理
 */
public class DBUtil {
    // 获取配置文件数据
    private static Properties properties;

    // 静态初始化
    static {
        try {
            InputStream inputStream = DBUtil.class.getClassLoader().getResourceAsStream("application.properties");
            properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     * @throws Exception
     * @description 获取数据库连接
     */
    public static Connection getConnection() throws Exception {
        String url = properties.getProperty("spring.datasource.url");
        String username = properties.getProperty("spring.datasource.username");
        String password = properties.getProperty("spring.datasource.password");
        String driverName = properties.getProperty("spring.datasource.driver-class-name");
        Class.forName(driverName);

        return DriverManager.getConnection(url, username, password);
    }

    /**
     *
     * @param connection
     * @throws Exception
     * @description 关闭数据库连接
     */
    public static void closeConnection(Connection connection) throws Exception {
        if(connection != null) {
            connection.close();
        }
    }
}
