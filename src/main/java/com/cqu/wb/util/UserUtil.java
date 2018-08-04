package com.cqu.wb.util;

/**
 * Created by jingquan on 2018/8/3.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cqu.wb.domain.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserUtil {

    /**
     *
     * @param count
     * @throws Exception
     * @description 生成5000条用户信息添加进数据库，并模拟登录功能生成相应token信息，存储进文件便于服务器模拟多用户进行压测
     */
    public static void createUser(int count) throws Exception {
        List<User> userList = new ArrayList<User>(count);
        // 生成用户
        for(int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13000000000L + i);
            user.setLoginCount(1);
            user.setNickname("user" + i);
            user.setRegisterDate(new Date());
            user.setSalt("1a2b3c");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            userList.add(user);
        }
        System.out.println("生成用户");

        // 插入数据库
        Connection connection = DBUtil.getConnection();
        String sql = "insert into user(login_count, nickname, register_date, salt, password, id)values(?,?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for(int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            preparedStatement.setInt(1, user.getLoginCount());
            preparedStatement.setString(2, user.getNickname());
            preparedStatement.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
            preparedStatement.setString(4, user.getSalt());
            preparedStatement.setString(5, user.getPassword());
            preparedStatement.setLong(6, user.getId());

            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        preparedStatement.close();
        DBUtil.closeConnection(connection);
        System.out.println("插入数据库");

        // 登录生成用户token，并写入文件
        String login_url = "http://localhost:8080/user/do_login";
        File file = new File("/Users/jingquan/Desktop/tokens.txt");
        if(file.exists()) {
            file.delete();
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        file.createNewFile();
        randomAccessFile.seek(0);
        for(int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            String params = "mobile="+user.getId()+"&password="+MD5Util.inputPassToFormPass("123456");
            outputStream.write(params.getBytes());
            outputStream.flush();

            InputStream inputStream = httpURLConnection.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while((length = inputStream.read(buffer)) >= 0) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            inputStream.close();
            byteArrayOutputStream.close();

            String response = new String(byteArrayOutputStream.toByteArray());
            JSONObject jsonObject = JSON.parseObject(response);
            String token = jsonObject.getString("data");
            System.out.println("生成用户token：" + user.getId());

            String row = user.getId() + "," + token;
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(row.getBytes());
            randomAccessFile.write("\r\n".getBytes());
            System.out.println("写入文件：" + user.getId());
        }
        randomAccessFile.close();
        System.out.println("任务成功");
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
