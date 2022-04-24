package BotStuff.SomethingNew.abstractClasses;

import java.sql.*;

public abstract class BotDb {

    protected static Connection connection;
    protected static Statement statement;
    protected static ResultSet resultSet;

    protected static void connectToSqlServer() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            connection = DriverManager.getConnection(
                    "jdbc:sqlserver://DESKTOP-5KD4VRL\\MSSQLSERVER2017:1433;DatabaseName=BotDB;IntegratedSecurity=true");

            statement = connection.createStatement();
        }
        catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    protected static void closeConnectionToSqlServer() {
        if (resultSet != null) {
            try {
                resultSet.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
