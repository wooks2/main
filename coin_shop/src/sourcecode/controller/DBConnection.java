package sourcecode.controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Enumeration;


public class DBConnection {
	
	private static Connection conn;

	private DBConnection() {
	}
	
	static {
		
    // ���� ���ڿ� ����
    // -���ӿ� �ʿ��� ������ ������ ���ڿ�, Connection String
    String url = "jdbc:oracle:thin:@localhost:1521/xepdb1";
    String id = "coin";
    String pw = "shop";
    
    // DB�۾� > �ܺ� ����� > try-catch �ʼ�
    try {
        // JDBC ����̹� �ε�
        Class.forName("oracle.jdbc.driver.OracleDriver");
        
        // DriverManager ( JDBC ����̹� �ν��Ͻ� ���� )
        // getDrivers() : ���� ������ JDBC �ν��Ͻ� ��� ��ȯ �޼ҵ�
        Enumeration<Driver> driverList = DriverManager.getDrivers();
        
        while(driverList.hasMoreElements()) {
        	// Driver �ν��Ͻ��� ��ȯ�޾Ƽ� ���
        	Driver driver = (Driver)driverList.nextElement();
            System.out.println(driver);
        }
        
        // ����
        // - Connection ��ü ���� + ���� �۾�.
        conn = DriverManager.getConnection(url, id, pw);
        System.out.println("conn = "+ conn);
        System.out.println(conn.isClosed()? "��������" : "������");// ������(false), ��������(true)
        
    } catch (ClassNotFoundException e) {
        System.out.println("Oracle Driver Ŭ���� ã�� �� ����" + e.getMessage());
    } catch (SQLException e) {
    	System.out.println("SQL ���� " + e.getMessage());
    }
   }
	
	
	public static Connection getConnection() {
		return conn;
	}
}
