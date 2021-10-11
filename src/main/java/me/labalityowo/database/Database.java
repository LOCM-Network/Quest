package me.labalityowo.database;

import me.labalityowo.Quest;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class Database {

    private static Connection connection;

    public static void initialize(){
        try {
            Class.forName("org.sqlite.JDBC");
            File file = new File(Quest.getInstance().getDataFolder().getAbsolutePath() + "/quests.db");
            file.createNewFile();
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void execute(String sql, DatabaseResultSet callable){
        Statement statement = null;
        try {
            statement = connection.createStatement();
            try(ResultSet resultSet = statement.executeQuery(sql)){
                callable.processData(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static void execute(String sql){
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
