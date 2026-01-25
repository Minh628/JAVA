/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Config: DatabaseHelper - Quản lý kết nối CSDL
 */
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    // Cấu hình kết nối MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/thi_trac_nghiem";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private static Connection connection = null;

    /**
     * Lấy kết nối tới CSDL
     * @return Connection đối tượng kết nối
     * @throws SQLException nếu không kết nối được
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL Driver not found", e);
            }
        }
        return connection;
    }

    /**
     * Đóng kết nối CSDL
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
