package com.rs;

import java.sql.*;

public class JdbcConnectTest {
    private static final String URL = "jdbc:mysql://localhost:3306/xsxx?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = System.getenv("RS_TEST_DB_PASSWORD");

    public static void main(String[] args) {
        Connection conn = null;
        try {
            if (PASSWORD == null || PASSWORD.isBlank()) {
                throw new IllegalStateException("RS_TEST_DB_PASSWORD is not configured");
            }
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false);
            System.out.println("事务开启...");

            String sql = "INSERT INTO C (CNO, CN, CT, TNO) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                // 正常数据
                pstmt.setString(1, "C6");
                pstmt.setString(2, "AI基础");
                pstmt.setByte(3, (byte) 48);
                pstmt.setString(4, "T006");
                pstmt.addBatch();
                // 故意插入重复主键（C6 已存在），触发异常
                pstmt.setString(1, "C6"); // 主键冲突！
                pstmt.setString(2, "重复课程");
                pstmt.setByte(3, (byte) 30);
                pstmt.setString(4, "T999");
                pstmt.addBatch();

                pstmt.executeBatch(); // 这里会抛出 SQLException
                conn.commit();
                System.out.println("事务提交成功。");

            } catch (SQLException e) {
                System.out.println("发生错误，正在回滚事务...");
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }



    public static void addCourse(Connection conn, String cno, String cn, Byte ct, String tno) throws SQLException {
        String sql = "INSERT INTO C (CNO, CN, CT, TNO) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cno);
            pstmt.setString(2, cn);
            pstmt.setByte(3, ct); // CT 是 TINYINT(1)，可用 byte
            pstmt.setString(4, tno);
            int rows = pstmt.executeUpdate();
            System.out.println("添加课程 " + cno + "，影响行数：" + rows);
        }
    }

    public static void updateCourse(Connection conn, String cno, Byte newCt) throws SQLException {
        String sql = "UPDATE C SET CT = ? WHERE CNO = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setByte(1, newCt);
            pstmt.setString(2, cno);
            int rows = pstmt.executeUpdate();
            System.out.println("更新课程 " + cno + " 的课时为 " + newCt + "，影响行数：" + rows);
        }
    }

    public static void deleteCourse(Connection conn, String cno) throws SQLException {
        String sql = "DELETE FROM C WHERE CNO = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cno);
            int rows = pstmt.executeUpdate();
            System.out.println("删除课程 " + cno + "，影响行数：" + rows);
        }
    }
}
